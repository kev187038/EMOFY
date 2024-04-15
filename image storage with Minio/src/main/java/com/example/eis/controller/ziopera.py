import requests
'''
url = 'http://localhost:8081/api/files/upload'
files = {'file': ('test.jpg', open('C:/Users/Pc/Desktop/queries.txt', 'rb'), 'text/plain')}  # Modifica il nome del file qui
data = {'user': 'Alexotan', 'label': 'ziopera'}
print(files)
print(data)
response = requests.post(url, files=files, data=data)
print(response.text)
'''
'''
url = 'http://localhost:8081/api/files/username/0000018e-e107-ec0b-8a1e-f9e90c4a7488'
data = {'user': 'username', 'file': ''}
response = requests.delete(url, data=data)
print(response.text)
'''
#username/0000018e-e107-ec0b-8a1e-f9e90c4a7488

url = 'http://localhost:8081/api/files/Alexotan/0000018e-e235-dc70-a4cf-54384e4ea215'
data = {'label': 'sad'}
response = requests.put(url, data=data)
print(response.text)
