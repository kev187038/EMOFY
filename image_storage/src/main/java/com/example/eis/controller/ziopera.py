import requests

url = 'http://localhost:8081/api/images/upload'
files = {'file': ('test.jpg', open("C:/Users/Pc/Desktop/EMOFY-master/EMOFY/image storage with Minio/src/main/resources/static/test.jpg", 'rb'), 'image/jpeg')}  #'image/jpeg')} Modifica il nome del file qui
data = {'user': 'Alex'}#, 'label': 'ziopera'}
print(files)
print(data)
response = requests.post(url, files=files, data=data)
print(response.text)

'''
url = 'http://localhost:8081/api/files/username/0000018e-e107-ec0b-8a1e-f9e90c4a7488'
data = {'user': 'username', 'file': ''}
response = requests.delete(url, data=data)
print(response.text)
'''
#username/0000018e-e107-ec0b-8a1e-f9e90c4a7488

# url = 'http://localhost:8081/api/files/Alexotan/0000018f-0107-cc2e-b28f-b02e4860f1bf'
# data = {'label': 'sad'}
# response = requests.put(url, data=data)
# print(response.text)

