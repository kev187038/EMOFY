from server import app
import cv2
import base64


if __name__ == '__main__':
    errors = 0
    with app.test_client() as client:
        img = cv2.imread('test_images/minivan.jpg')
        _, buffer = cv2.imencode('.jpg', img)
        img_str = base64.b64encode(buffer).decode('utf-8')
        response = client.post('/detect_face', json={'image': img_str})
        assert response.status_code == 200
        assert response.json['face_detected'] == False


        img = cv2.imread('test_images/happy-man.jpg')
        _, buffer = cv2.imencode('.jpg', img)
        img_str = base64.b64encode(buffer).decode('utf-8')
        response = client.post('/detect_emotion', json={'image': img_str})
        assert response.status_code == 200
        try: assert response.json['emotion']['emotion'] == 'happy'
        except AssertionError: 
            errors+=1
            print(f"Expected happy but got {response.json['emotion']['emotion']}")



        img = cv2.imread('test_images/disgusted-man.jpg')
        _, buffer = cv2.imencode('.jpg', img)
        img_str = base64.b64encode(buffer).decode('utf-8')
        response = client.post('/detect_emotion', json={'image': img_str})
        assert response.status_code == 200
        try: assert response.json['emotion']['emotion'] == 'disgust'
        except AssertionError:
            errors+=1
            print(f"Expected disgust but got {response.json['emotion']['emotion']}")



        img = cv2.imread('test_images/disgusted.jpg')
        _, buffer = cv2.imencode('.jpg', img)
        img_str = base64.b64encode(buffer).decode('utf-8')
        response = client.post('/detect_emotion', json={'image': img_str})
        assert response.status_code == 200
        try: assert response.json['emotion']['emotion'] == 'disgust'
        except AssertionError: 
            errors+=1
            print(f"Expected disgust but got {response.json['emotion']['emotion']}")



        img = cv2.imread('test_images/happy.jpg')
        _, buffer = cv2.imencode('.jpg', img)
        img_str = base64.b64encode(buffer).decode('utf-8')
        response = client.post('/detect_emotion', json={'image': img_str})
        assert response.status_code == 200
        try: assert response.json['emotion']['emotion'] == 'happy'
        except AssertionError:
            errors+=1
            print(f"Expected happy but got {response.json['emotion']['emotion']}")

        img = cv2.imread('test_images/angry.jpeg')
        _, buffer = cv2.imencode('.jpeg', img)
        img_str = base64.b64encode(buffer).decode('utf-8')
        response = client.post('/detect_emotion', json={'image': img_str})
        assert response.status_code == 200
        try: assert response.json['emotion']['emotion'] == 'angry'
        except AssertionError: 
            errors+=1
            print(f"Expected angry but got {response.json['emotion']['emotion']}")
        
        
        try: assert errors == 0
        except AssertionError:
            print(f"Test failed with {errors} errors")
            raise AssertionError

