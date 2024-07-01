docker login
cd ./login
call mvn clean install
docker build -t emofy-login-service:latest .
cd ..
docker tag emofy-login-service:latest siralex01/emofy-login-service:latest
docker push siralex01/emofy-login-service:latest