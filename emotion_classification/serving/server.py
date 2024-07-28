from flask import Flask, request, jsonify
from face_detector import FaceDetector
from emotion_detector import EmotionDetector

app = Flask(__name__)


@app.route('/detect_face', methods=['POST'])
def detect_face():
    data = request.json
    base64_img = data['image']
    return jsonify({'face_detected': FaceDetector().detect_face(base64_img)})

@app.route('/detect_emotion', methods=['POST'])
def detect_emotion():
    data = request.json
    base64_img = data['image']
    return jsonify({'emotion': EmotionDetector().detect_emotion(base64_img)})