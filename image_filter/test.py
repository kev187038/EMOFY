import os
import base64
import cv2
import numpy as np
from Image import Image

output_dir = 'output_images'
if not os.path.exists(output_dir):
    os.makedirs(output_dir)

image = cv2.imread('test.jpg')

base64_image = base64.b64encode(cv2.imencode('.jpg', image)[1]).decode()
image_instance = Image(base64_image)

# Filtro scala di grigi
gray = image_instance.apply_filter('gray')
gray = cv2.imdecode(np.frombuffer(base64.b64decode(gray), np.uint8), cv2.IMREAD_GRAYSCALE)
cv2.imwrite(os.path.join(output_dir, 'gray.jpg'), gray)

# Filtro sfocatura
blur = image_instance.apply_filter('blur')
blur = cv2.imdecode(np.frombuffer(base64.b64decode(blur), np.uint8), cv2.IMREAD_COLOR)
cv2.imwrite(os.path.join(output_dir, 'blur.jpg'), blur)

# Filtro "canny"
canny = image_instance.apply_filter('canny')
canny = cv2.imdecode(np.frombuffer(base64.b64decode(canny), np.uint8), cv2.IMREAD_GRAYSCALE)
cv2.imwrite(os.path.join(output_dir, 'canny.jpg'), canny)

#Filtro "Dilate"
dilate = image_instance.apply_filter('dilate')
dilate = cv2.imdecode(np.frombuffer(base64.b64decode(dilate), np.uint8), cv2.IMREAD_COLOR)
cv2.imwrite(os.path.join(output_dir, 'dilate.jpg'), dilate)

# Filtro "Erode"
erode = image_instance.apply_filter('erode')
erode = cv2.imdecode(np.frombuffer(base64.b64decode(erode), np.uint8), cv2.IMREAD_COLOR)
cv2.imwrite(os.path.join(output_dir, 'erode.jpg'), erode)

# Filtro Seppia
sepia = image_instance.apply_filter('sepia')
sepia = cv2.imdecode(np.frombuffer(base64.b64decode(sepia), np.uint8), cv2.IMREAD_COLOR)
cv2.imwrite(os.path.join(output_dir, 'sepia.jpg'), sepia)

# Filtro "cartoonify"
cartoonify = image_instance.apply_filter('cartoonify')
cartoonify = cv2.imdecode(np.frombuffer(base64.b64decode(cartoonify), np.uint8), cv2.IMREAD_COLOR)
cv2.imwrite(os.path.join(output_dir, 'cartoonify.jpg'), cartoonify)

# Filtro negativo
negative = image_instance.apply_filter('negative')
negative = cv2.imdecode(np.frombuffer(base64.b64decode(negative), np.uint8), cv2.IMREAD_COLOR)
cv2.imwrite(os.path.join(output_dir, 'negative.jpg'), negative)

# Filtro rilievo
emboss = image_instance.apply_filter('emboss')
emboss = cv2.imdecode(np.frombuffer(base64.b64decode(emboss), np.uint8), cv2.IMREAD_COLOR)
cv2.imwrite(os.path.join(output_dir, 'emboss.jpg'), emboss)

# Filtro "Sharpen"
sharpen = image_instance.apply_filter('sharpen')
sharpen = cv2.imdecode(np.frombuffer(base64.b64decode(sharpen), np.uint8), cv2.IMREAD_COLOR)
cv2.imwrite(os.path.join(output_dir, 'sharpen.jpg'), sharpen)

# Filtro "Edge Enhance"
edge_enhance = image_instance.apply_filter('edge_enhance')
edge_enhance = cv2.imdecode(np.frombuffer(base64.b64decode(edge_enhance), np.uint8), cv2.IMREAD_COLOR)
cv2.imwrite(os.path.join(output_dir, 'edge_enhance.jpg'), edge_enhance)

# Filtro "Rainbowify"
rainbowify = image_instance.apply_filter('rainbowify')
rainbowify = cv2.imdecode(np.frombuffer(base64.b64decode(rainbowify), np.uint8), cv2.IMREAD_COLOR)
cv2.imwrite(os.path.join(output_dir, 'rainbowify.jpg'), rainbowify)

# Filtro specchiato
mirror = image_instance.apply_filter('mirror')
mirror = cv2.imdecode(np.frombuffer(base64.b64decode(mirror), np.uint8), cv2.IMREAD_COLOR)
cv2.imwrite(os.path.join(output_dir, 'mirror.jpg'), mirror)

# Sottosopra
flip = image_instance.apply_filter('flip')
flip = cv2.imdecode(np.frombuffer(base64.b64decode(flip), np.uint8), cv2.IMREAD_COLOR)
cv2.imwrite(os.path.join(output_dir, 'flip.jpg'), flip)

# Rotazione oraria
rotate_clockwise = image_instance.apply_filter('rotate_clockwise')
rotate_clockwise = cv2.imdecode(np.frombuffer(base64.b64decode(rotate_clockwise), np.uint8), cv2.IMREAD_COLOR)
cv2.imwrite(os.path.join(output_dir, 'rotate_clockwise.jpg'), rotate_clockwise)

# Rotazione antioraria
rotate_counterclockwise = image_instance.apply_filter('rotate_counterclockwise')
rotate_counterclockwise = cv2.imdecode(np.frombuffer(base64.b64decode(rotate_counterclockwise), np.uint8), cv2.IMREAD_COLOR)
cv2.imwrite(os.path.join(output_dir, 'rotate_counterclockwise.jpg'), rotate_counterclockwise)
