import base64
import cv2
import numpy as np
import logging

class Image:
    def __init__(self, image):
        self.base64_image = image
        self.image = base64.b64decode(image)
        if not self.is_valid_image(self.image):
            raise InvalidImageError("Invalid image format or data")
        self.np_image = cv2.imdecode(np.frombuffer(self.image, np.uint8), cv2.IMREAD_COLOR)

    def is_valid_image(self, image):
        try:
            np.frombuffer(image, np.uint8)
            return True
        except Exception as e:
            return False
    
    def apply_filter(self, filter):
        res = None
        if filter == 'gray': res = self._gray()
        if filter == 'blur': res = self._blur()
        if filter == 'canny': res = self._canny()
        if filter == 'dilate': res = self._dilate()
        if filter == 'erode': res = self._erode()
        if filter == 'sepia': res = self._sepia()
        if filter == 'cartoonify': res = self._cartoonify()
        if filter == 'negative': res = self._negative()
        if filter == 'emboss': res = self._emboss()
        if filter == 'sharpen': res = self._sharpen()
        if filter == 'edge_enhance': res = self._edge_enhance()
        if filter == 'rainbowify': res = self._rainbowify()
        if filter == 'mirror': res = self._mirror()
        if filter == 'flip': res = self._flip()
        if filter == 'rotate_clockwise': res = self._rotate_clockwise()
        if filter == 'rotate_counterclockwise': res = self._rotate_counterclockwise()
        if res is not None:
            return self._encode(res)
        raise Exception("Invalid filter")

    def _gray(self):
        return cv2.cvtColor(self.np_image, cv2.COLOR_BGR2GRAY)
    
    def _blur(self):
        return cv2.GaussianBlur(self.np_image, (21, 21), 0)
    
    def _canny(self):
        return cv2.Canny(self.np_image, 100, 200)
    
    def _dilate(self):
        kernel = np.ones((5, 5), np.uint8)
        return cv2.dilate(self.np_image, kernel, iterations=1)
    
    def _erode(self):
        kernel = np.ones((5, 5), np.uint8)
        return cv2.erode(self.np_image, kernel, iterations=1)
    
    def _sepia(self):
        kernel = np.array([[0.272, 0.534, 0.131],
                           [0.349, 0.686, 0.168],
                           [0.393, 0.769, 0.189]])
        return cv2.transform(self.np_image, kernel)
    
    def _cartoonify(self):
        gray = cv2.cvtColor(self.np_image, cv2.COLOR_BGR2GRAY)
        gray = cv2.medianBlur(gray, 5)
        edges = cv2.adaptiveThreshold(gray, 255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY, 9, 9)
        color = cv2.bilateralFilter(self.np_image, 9, 300, 300)
        return cv2.bitwise_and(color, color, mask=edges)

    def _negative(self):
        return cv2.bitwise_not(self.np_image)

    def _emboss(self):
        kernel = np.array([[0, -1, -1],
                           [1,  0, -1],
                           [1,  1,  0]])
        return cv2.filter2D(self.np_image, -1, kernel)

    def _sharpen(self):
        kernel = np.array([[0, -1, 0],
                           [-1, 5, -1],
                           [0, -1, 0]])
        return cv2.filter2D(self.np_image, -1, kernel)

    def _edge_enhance(self):
        kernel = np.array([[-1, -1, -1],
                           [-1,  9, -1],
                           [-1, -1, -1]])
        return cv2.filter2D(self.np_image, -1, kernel)

    def _rainbowify(self):
        h, w, _ = self.np_image.shape
        rainbow = np.zeros((h, w, 3), np.uint8)
        rainbow[:, :, 0] = 255  # Blue
        rainbow[:, :, 1] = np.clip(np.sin(np.arange(w) * 0.01) * 255, 0, 255)  # Green
        rainbow[:, :, 2] = 255 - rainbow[:, :, 1]  # Red
        return cv2.addWeighted(self.np_image, 0.7, rainbow, 0.3, 0)

    def _mirror(self):
        return cv2.flip(self.np_image, 1)

    def _flip(self):
        return cv2.flip(self.np_image, 0)

    def _rotate_clockwise(self):
        return np.rot90(self.np_image, -1)

    def _rotate_counterclockwise(self):
        return np.rot90(self.np_image)
    
    def _encode(self, image):
        _, buffer = cv2.imencode('.jpg', image)
        return base64.b64encode(buffer).decode('utf-8')
