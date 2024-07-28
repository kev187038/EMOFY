import json
import requests
from bs4 import BeautifulSoup

# URLs

class LoginError(Exception):
    pass

class CSRFTokenError(Exception):
    pass

class ImageRetrievalError(Exception):
    pass

class ImageRetriever:
    def __init__(self, login_url, images_url, login_data):
        self.login_url = login_url
        self.images_url = images_url
        self.login_data = login_data
        with requests.Session() as session:
            csrf_token_name, csrf_token_value = self.get_csrf_token(session)
            self.session_token = self.login(session, csrf_token_name, csrf_token_value)
        self.cookies = {'SESSION_TOKEN': self.session_token}
        print('Session token:', self.session_token)

    def get_csrf_token(self, session):
        try:
            response = session.get(self.login_url)
            response.raise_for_status()
            
            soup = BeautifulSoup(response.text, 'html.parser')
            csrf_token_input = soup.find('input', {'type': 'hidden'})
            
            if csrf_token_input:
                return csrf_token_input.get('name'), csrf_token_input.get('value')
            else:
                raise CSRFTokenError('CSRF token non trovato nella pagina di login')
        except requests.RequestException as e:
            raise CSRFTokenError(f'Errore durante la richiesta della pagina di login: {e}')

    def login(self, session, csrf_token_name, csrf_token_value):
        try:
            login_data = self.login_data.copy()
            login_data[csrf_token_name] = csrf_token_value

            response = session.post(self.login_url, data=login_data, allow_redirects=False)
            session_token = response.cookies.get('SESSION_TOKEN')
            if session_token:
                return session_token
            else:
                raise LoginError('Token di sessione non trovato nella risposta di login')
        except requests.RequestException as e:
            raise LoginError(f'Errore durante la richiesta di login: {e}')

    def get_image_names(self):
        try: 
            response = requests.get(self.images_url + f'/{self.login_data["id"]}', cookies=self.cookies) #TODO:CAPIRE COME OTTENERE L'ID
            response.raise_for_status()
            try: data = json.loads(response.text)['data']
            except json.decoder.JSONDecodeError: return []
            return [{'id': key, **value} for key, value in data.items()]
        except requests.RequestException as e:
            raise ImageRetrievalError(f'Errore nella richiesta delle immagini: {e}')

    def get_image(self, image_name):
        try:
            print(f'{self.images_url}/{image_name}')
            response = requests.get(f'{self.images_url}/{image_name}', cookies=self.cookies)
            response.raise_for_status()
            return response.content
        except requests.RequestException as e:
            raise ImageRetrievalError(f'Errore nel download dell\'immagine {image_name}: {e}')

def main():
    retriever = ImageRetriever(LOGIN_URL, IMAGES_URL, LOGIN_DATA)
    try:
        images = retriever.perform_login_and_get_images()
        print('Immagini ricevute:', images)
    except (CSRFTokenError, LoginError, ImageRetrievalError) as e:
        print(f'Errore: {e}')

