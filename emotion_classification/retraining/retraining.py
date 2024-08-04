import os
import sys
import cv2
from minio import Minio
from minio.error import S3Error
from kubernetes import client, config
from tensorflow.keras.models import load_model
from cronjob_monitor import CronJobMonitor
from image_retriever import ImageRetriever
from tensorflow.keras.preprocessing.image import ImageDataGenerator
os.environ['TF_ENABLE_ONEDNN_OPTS'] = '0'
config.load_kube_config()
#sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from face_detector import FaceDetector
from minio import MinioClient
import time

# script periodico per aggiornamento modello


# Download del dataset
v1 = client.CoreV1Api()
service = v1.read_namespaced_service(name='emofy-login-service', namespace='default')
login_page_url = f"http://{service.spec.cluster_ip}:{service.spec.ports[0].port}/login"

service = v1.read_namespaced_service(name='emofy-image-service', namespace='default')
images_url = f"http://{service.spec.cluster_ip}:{service.spec.ports[0].port}/api/images/"

login_data = {
    'username': os.getenv('LOGIN_USERNAME'),
    'password': os.getenv('LOGIN_PASSWORD'),
    'id': "1"
}


retriever = ImageRetriever(login_page_url, images_url, login_data)
images = retriever.get_image_names()

monitor = CronJobMonitor('default', 'retrain-cronjob')
last_timestamp = monitor.last_execution()
if last_timestamp:
    images = [image for image in images if image['timestamp'] > last_timestamp]

# keep only those images having the key 'label'
images = [image for image in images if 'label' in image]

incremental_dir = 'incremental_learning_images'
images_path = []
for image in images:
    folder = '{}/{}'.format(incremental_dir, image['label'])
    if not os.path.exists(folder):
        os.makedirs(folder)
    path = os.path.join(folder, image['filename'])
    images_path.append(path)
    with open(path, 'wb') as f:
        f.write(retriever.get_image(image['id']))

# Now, replace each image with only the face
face_detector = FaceDetector()
for path in images_path:
    img = cv2.imread(path)
    face_img = face_detector.detect_face(img, decode=False)
    if face_img is False:
        os.remove(path)
    else:
        cv2.imwrite(path, face_img)

# 3) Aggiornamento del modello
minio = MinioClient()

model_bucket = os.getenv('MODEL_BUCKET')

models = [{'name':model.object_name, 'time':model.last_modified} for model in minio.list_files(model_bucket)]
models.sort(key=lambda x: x['time'])
minio.download_file('models', models[0]['name'], 'model.h5')
model = load_model('model.h5')  # TODO: prendi ultimo in ordine cronologico

# Recompile the model
model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])

new_data_dir = 'incremental_learning_images'
emotions = ['angry', 'disgust', 'fear', 'happy', 'neutral', 'sad', 'surprise']
# check that there is a folder inside new_data_dir for each emotion, otherwise create it

# delete any folder not in emotions
for folder in os.listdir(new_data_dir):
    if folder not in emotions:
        os.rmdir(folder)
num_images = 0
for emotion in emotions:
    if not os.path.exists(f'{new_data_dir}/{emotion}'):
        os.makedirs(f'{new_data_dir}/{emotion}')
    num_images += len(os.listdir(f'{new_data_dir}/{emotion}'))

if not num_images: sys.exit(0)

# Create an ImageDataGenerator without data augmentation for the new data
new_data_datagen = ImageDataGenerator(rescale=1.0/255)
#count number of images in each folder
for emotion in emotions:
    num_images += len(os.listdir(f'{new_data_dir}/{emotion}'))


# Rischio di Catastrophic Forgetting #TODO DISCUTERNE

# Create a generator to read images from the new data directory
new_data_generator = new_data_datagen.flow_from_directory(
    new_data_dir,
    target_size=(48, 48),
    color_mode='grayscale',
    batch_size=64,
    class_mode='categorical'
)

# Retrain the model on the new data
history = model.fit(
    new_data_generator,
    steps_per_epoch=new_data_generator.samples // new_data_generator.batch_size,
    epochs=10  # You can adjust the number of epochs based on your needs
)

# Save the updated model
model_name = 'model_' + time.strftime('%Y%m%d%H%M%S') + '.h5'
model.save(model_name)
minio.upload_file('models', model_name, model_name)
