import cv2
import numpy as np
import base64

class FaceDetector:

    def __init__(self):
        self.face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')

    @staticmethod
    def decode_image(base64_str):
        img_data = base64.b64decode(base64_str)
        np_arr = np.frombuffer(img_data, np.uint8)
        img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)
        return img

    def detect_face(self, base64_img, decode = True):
        img = self.decode_image(base64_img) if decode else base64_img
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        faces = self.face_cascade.detectMultiScale(gray, 1.3, 5)
        # Check if at least one face is detected
        if len(faces) == 0:
            print("returning false")
            return False

        # Extract and return the first detected face
        x, y, w, h = faces[0]
        face_img = img[y:y+h, x:x+w]
        return face_img
