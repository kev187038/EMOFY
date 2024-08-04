from flask import Flask, request, jsonify
from utils.face_detector import FaceDetector
from utils.emotion_detector import EmotionDetector
from utils.minio import MinioClient
from utils.logger import set_logger
import os

app = Flask(__name__)

minio = MinioClient()

log = set_logger('serving')
#get the model bucket name from the environment variable
model_bucket = os.getenv('MODEL_BUCKET')
model_name = os.getenv('MODEL_NAME')

if not minio.minio_client.bucket_exists(model_bucket):
    minio.minio_client.make_bucket(model_bucket)
    minio.upload_file(model_bucket, model_name, model_name)
else:
    minio.download_file(model_bucket, model_name, model_name) #TODO: Rendi ultimo modello disponibile


@app.route('/detect_face', methods=['POST'])
def detect_face():
    log.info('Face detection request received')
    data = request.json
    base64_img = data['image']
    return jsonify({'face_detected': FaceDetector().detect_face(base64_img)})

@app.route('/detect_emotion', methods=['POST'])
def detect_emotion():
    log.info('Emotion detection request received')
    data = request.json
    base64_img = data['image']
    emotion = EmotionDetector().detect_emotion(base64_img)
    log.info(f'Emotion detected: {emotion}')
    return jsonify({'emotion': emotion})

if __name__ == '__main__':
    from waitress import serve
    log.info('Starting the server')
    try:
        serve(app, host='0.0.0.0', port=5050)
    except Exception as e:
        log.error(f'Error: {e}')
        log.error('Shutting down the server')
        exit(1)