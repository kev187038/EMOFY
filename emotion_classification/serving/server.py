from flask import Flask, request, jsonify
from utils.face_detector import FaceDetector
from utils.emotion_detector import EmotionDetector
from utils.minio_client import MinioClient
from utils.logger import set_logger
import os
from flask_cors import CORS 

log = set_logger('serving')

app = Flask(__name__)
CORS(app, resources={
    r"/detect_face": {"origins": "http://localhost:8081"},
    r"/detect_emotion": {"origins": "http://localhost:8081"}
})

os.environ["CUDA_VISIBLE_DEVICES"] = "-1"
minio = MinioClient()

#get the model bucket name from the environment variable
model_bucket = os.getenv('MODEL_BUCKET')
model_name = None
detector = None

def latest_model_available():
    models = [{'name': model.object_name, 'time': model.last_modified} for model in minio.list_files(model_bucket)]
    models.sort(key=lambda x: x['time'])
    new_model_name = models[-1]['name']
    return new_model_name

def update_model():
    global model_name
    global detector
    new_model_name = latest_model_available()
    if not model_name or new_model_name != model_name: #if the latest model is different from the current model
        model_name = new_model_name
        minio.download_file(model_bucket, new_model_name, 'model.keras')
        log.info(f'[EMOFY] : Downloaded new model {new_model_name}')
        log.info('[EMOFY] : Now using new model for emotion detection')
        detector = EmotionDetector()

if not minio.minio_client.bucket_exists(model_bucket):
    minio.minio_client.make_bucket(model_bucket)
    minio.upload_file(model_bucket, "model.keras", "model.keras")

@app.route('/detect_face', methods=['POST'])
def detect_face():
    log.info('[EMOFY] : Face detection request received')
    data = request.json
    base64_img = data['image']
    
    # Call the FaceDetector
    face_detected = FaceDetector().detect_face(base64_img)
    
    # Return True if a face is detected, otherwise False
    if face_detected is False:
        return jsonify({'face_detected': False})
    else:
        return jsonify({'face_detected': True})

@app.route('/detect_emotion', methods=['POST'])
def detect_emotion():
    log.info('[EMOFY] : Emotion detection request received')
    update_model()
    data = request.json
    base64_img = data['image']
    file_key = data['fileKey']  # Get the fileKey from the request
    emotion = detector.detect_emotion(base64_img)
    log.info(f'[EMOFY] : Emotion detected: {emotion}')
    return jsonify({
        'emotion': emotion,
        'fileKey': file_key  # Include the fileKey in the response
    })

if __name__ == '__main__':
    from waitress import serve
    log.info('[EMOFY] : Starting the server for emotion classification')
    try:
        serve(app, host='0.0.0.0', port=5050)
    except Exception as e:
        log.error(f'Error: {e}')
        log.error('[EMOFY] : Shutting down the server for emotion classification')
        exit(1)
