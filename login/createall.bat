kubectl create -f ./deployment/emofy-db-container-claim0-persistentvolumeclaim.yaml
kubectl create -f ./deployment/emofy-db-container-deployment.yaml
kubectl create -f ./service/emofy-db-container-service.yaml

REM mvn clean install
docker login
docker build -t emofy-login-service:latest .
docker tag emofy-login-service:latest siralex01/emofy-login-service:latest
docker push siralex01/emofy-login-service:latest

kubectl create -f ./deployment/emofy-login-service-deployment.yaml
kubectl create -f ./service/emofy-login-service-service.yaml
