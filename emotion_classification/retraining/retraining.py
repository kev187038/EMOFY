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

config.load_kube_config()
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from serving.utils.face_detector import FaceDetector


#script periodico per aggiornamento modello


#1) Download del modello da minio
v1 = client.CoreV1Api()
service = v1.read_namespaced_service(name='minio-service', namespace='default')
minio_service = f"{service.spec.cluster_ip}:{service.spec.ports[0].port}"

minio_client = Minio(
    minio_service,  # Replace with your MinIO server URL
    access_key=os.getenv('MINIO_ACCESS_KEY'),  # Replace with your access key
    secret_key=os.getenv('MINIO_SECRET_KEY'),  # Replace with your secret key
    secure=False  # Set to False if your MinIO server is not using HTTPS
)

try: minio_client.fget_object(os.getenv('MODEL_BUCKET'), os.getenv('MODEL_NAME'), 'model.h5')
except S3Error as err: print(f"Error occurred: {err}")


#2) Download del dataset
v1 = client.CoreV1Api()
service = v1.read_namespaced_service(name='emofy-login-service', namespace='default')
login_page_url = f"http://{service.spec.cluster_ip}:{service.spec.ports[0].port}/login"



service = v1.read_namespaced_service(name='emofy-image-service', namespace='default')
images_url = f"http://{service.spec.cluster_ip}:{service.spec.ports[0].port}/api/images/"


login_data = {
    'username': os.getenv('LOGIN_USERNAME'),
    'password': os.getenv('LOGIN_PASSWORD'),
    'id': "1" #TODO:CAPIRE COME OTTENERE L'ID
}


retriever = ImageRetriever(login_page_url, images_url, login_data)
images = retriever.get_image_names()

monitor = CronJobMonitor('default', 'retrain-cronjob')
last_timestamp = monitor.last_execution()
if last_timestamp: images = [image for image in images if image['timestamp'] > last_timestamp]

#keep only those images havingthe key 'label'
images = [image for image in images if 'label' in image]

print(images)
incremental_dir = 'incremental_learning_images'
images_path = []
for image in images:
    folder = '{}/{}'.format(incremental_dir, image['label'])
    if not os.path.exists(folder): os.makedirs(folder)
    path = os.path.join(folder, image['filename'])
    images_path.append(path)
    with open(path, 'wb') as f: f.write(retriever.get_image(image['id']))


#Now, replace each image with only the face
face_detector = FaceDetector()
for path in images_path:
    img = cv2.imread(path)
    face_img = face_detector.detect_face(img, decode=False)
    if face_img is False: os.remove(path)
    else: cv2.imwrite(path, face_img)

#3) Aggiornamento del modello
model = load_model('model.h5') #TODO: prendi ultimo in ordine cronologico
# Path to the new data
new_data_dir = 'incremental_learning_images'

# Create an ImageDataGenerator without data augmentation for the new data
new_data_datagen = ImageDataGenerator(rescale=1.0/255)

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
model.save('emotion_recognition_model_updated.h5')