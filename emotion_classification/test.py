from server import app
import cv2
import base64

if __name__ == '__main__':
    with app.test_client() as client:
        img = cv2.imread('test_images/minivan.jpg')
        _, buffer = cv2.imencode('.jpg', img)
        img_str = base64.b64encode(buffer).decode('utf-8')
        response = client.post('/detect_face', json={'image': img_str})
        assert response.status_code == 200
        assert response.json['face_detected'] == False

        img = cv2.imread('test_images/happy.jpg')
        _, buffer = cv2.imencode('.jpg', img)
        img_str = base64.b64encode(buffer).decode('utf-8')
        response = client.post('/detect_face', json={'image': img_str})
        assert response.status_code == 200
        assert response.json['face_detected'] == True

        img = cv2.imread('test_images/angry.jpeg')
        _, buffer = cv2.imencode('.jpeg', img)
        img_str = base64.b64encode(buffer).decode('utf-8')
        response = client.post('/detect_emotion', json={'image': img_str})
        assert response.status_code == 200
        assert response.json['emotion']['emotion'] == 'angry'
