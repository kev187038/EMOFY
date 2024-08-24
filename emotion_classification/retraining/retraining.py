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
config.load_incluster_config()
from face_detector import FaceDetector
from minio_client import MinioClient
import time
from logger import set_logger

# script periodico per aggiornamento modello

logger = set_logger('retraining')
logger.info('[EMOFY] : Starting retraining script')

# Download del dataset
v1 = client.CoreV1Api()
service = v1.read_namespaced_service(name='emofy-login-service', namespace='default')
login_page_url = f"http://{service.spec.cluster_ip}:{service.spec.ports[0].port}/login"
logger.info(f'[EMOFY] : Login page URL: {login_page_url}')

service = v1.read_namespaced_service(name='emofy-image-storage', namespace='default')
images_url = f"http://{service.spec.cluster_ip}:{service.spec.ports[0].port}/api/images/"
logger.info(f'[EMOFY] : Images URL: {images_url}')

login_data = {
    'username': os.getenv('LOGIN_USERNAME'),
    'password': os.getenv('LOGIN_PASSWORD'),
    'id': "1"
}

logger.info('[EMOFY] : Retrieving images')
retriever = ImageRetriever(login_page_url, images_url, login_data)
images = retriever.get_image_names()
logger.info(f'[EMOFY] : Total images retrieved: {len(images)}')

monitor = CronJobMonitor('default', 'emofy-retraining')
last_timestamp = monitor.last_execution()
if last_timestamp:
    logger.info(f'[EMOFY] : Filtering images based on last execution timestamp: {last_timestamp}')
    images = [image for image in images if image['timestamp'] > last_timestamp]

# Keep only those images having the key 'label'
images = [image for image in images if 'label' in image]
logger.info(f'[EMOFY] : Total labeled images: {len(images)}')

incremental_dir = 'incremental_learning_images'
images_path = []
for image in images:
    folder = '{}/{}'.format(incremental_dir, image['label'])
    if not os.path.exists(folder):
        os.makedirs(folder)
        logger.info(f'[EMOFY] : Created directory: {folder}')
    path = os.path.join(folder, image['filename'])
    images_path.append(path)
    with open(path, 'wb') as f:
        f.write(retriever.get_image(image['id']))
        logger.info(f'[EMOFY] : Downloaded and saved image: {path}')

# Now, replace each image with only the face
face_detector = FaceDetector()
for path in images_path:
    img = cv2.imread(path)
    face_img = face_detector.detect_face(img, decode=False)
    if face_img is False:
        os.remove(path)
        logger.info(f'[EMOFY] : Removed image without detectable face: {path}')
    else:
        cv2.imwrite(path, face_img)
        logger.info(f'[EMOFY] : Saved face image: {path}')

# 3) Aggiornamento del modello
minio = MinioClient()
model_bucket = os.getenv('MODEL_BUCKET')

logger.info('[EMOFY] : Retrieving models from Minio')
models = [{'name': model.object_name, 'time': model.last_modified} for model in minio.list_files(model_bucket)]
models.sort(key=lambda x: x['time'])
logger.info(f'[EMOFY] : Models available: {models}')

if models:
    model_name = models[-1]['name']
    minio.download_file('models', model_name, model_name)
    logger.info(f'[EMOFY] : Downloaded model: {model_name}')
    model = load_model(model_name)
    logger.info('[EMOFY] : Loaded model from file')
else:
    logger.error('[EMOFY] : No models found in the bucket')
    sys.exit(1)

# Recompile the model
model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])
logger.info('[EMOFY] : Model compiled')

new_data_dir = 'incremental_learning_images'
emotions = ['angry', 'disgust', 'fear', 'happy', 'neutral', 'sad', 'surprise']

# Delete any folder not in emotions and create directories if they do not exist
for folder in os.listdir(new_data_dir):
    if folder not in emotions:
        os.rmdir(os.path.join(new_data_dir, folder))
        logger.info(f'[EMOFY] : Removed unused directory: {folder}')

num_images = 0
for emotion in emotions:
    emotion_dir = os.path.join(new_data_dir, emotion)
    if not os.path.exists(emotion_dir):
        os.makedirs(emotion_dir)
        #logger.info(f'Created directory for emotion: {emotion}')
    num_images += len(os.listdir(emotion_dir))

if num_images == 0:
    logger.info('[EMOFY] : No new images to train on. Exiting.')
    sys.exit(0)

# Create an ImageDataGenerator without data augmentation for the new data
new_data_datagen = ImageDataGenerator(rescale=1.0/255)

logger.info(f'[EMOFY] : Total number of new images: {num_images}')

# Create a generator to read images from the new data directory
new_data_generator = new_data_datagen.flow_from_directory(
    new_data_dir,
    target_size=(48, 48),
    color_mode='grayscale',
    batch_size=64,
    class_mode='categorical'
)

logger.info('[EMOFY] : Starting model retraining')
history = model.fit(
    new_data_generator,
    steps_per_epoch=new_data_generator.samples // new_data_generator.batch_size,
    epochs=10  # You can adjust the number of epochs based on your needs
)

# Save the updated model
model_name = 'model_' + time.strftime('%Y%m%d%H%M%S') + '.keras'
model.save(model_name)
logger.info(f'[EMOFY] : Model saved as: {model_name}')

minio.upload_file('models', model_name, model_name)
logger.info(f'[EMOFY] : Uploaded model to Minio: {model_name}')
