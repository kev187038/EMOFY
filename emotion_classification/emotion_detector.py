
import base64
import numpy as np
import cv2
from io import BytesIO
from PIL import Image
from tensorflow.keras.models import load_model

class EmotionDetector:

    def __init__(self):
        # Initialize the face detector
        from face_detector import FaceDetector
        self.face_detector = FaceDetector()
        
        # Load the pre-trained emotion recognition model
        self.model = load_model('emotion_recognition_model.h5')
        
        # Emotion labels
        self.emotion_labels = {0: 'angry', 1: 'disgust', 2: 'fear', 3: 'happy', 4: 'neutral', 5: 'sad', 6: 'surprise'}

    def detect_emotion(self, base64_img):
        # Detect face in the image
        if not self.face_detector.detect_face(base64_img):
            return {'error': 'No face detected'}
        
        image = self._decode_base64(base64_img)
        # Preprocess the image
        preprocessed_img = self._preprocess_image(image)
        # Predict the emotion
        emotion = self._predict_emotion(preprocessed_img)
        # Return the result
        return {'emotion': emotion}

    def _decode_base64(self, base64_img):
        # Decode the base64 image
        image_data = base64.b64decode(base64_img)
        image = Image.open(BytesIO(image_data)).convert('RGB')
        return np.array(image)

    def _preprocess_image(self, image):
        # Convert to grayscale
        gray_img = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
        
        # Resize the image to match model input
        resized_img = cv2.resize(gray_img, (48, 48))
        
        # Normalize the image
        normalized_img = resized_img.astype('float32') / 255.0
        
        # Add batch dimension
        preprocessed_img = np.expand_dims(normalized_img, axis=0)
        preprocessed_img = np.expand_dims(preprocessed_img, axis=-1)
        
        return preprocessed_img

    def _predict_emotion(self, preprocessed_img):
        # Predict the emotion
        prediction = self.model.predict(preprocessed_img)
        emotion_class = np.argmax(prediction)
        return self.emotion_labels.get(emotion_class, 'Unknown')
