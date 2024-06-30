kubectl create -f ./login/deployment/emofy-db-container-claim0-persistentvolumeclaim.yaml
kubectl create -f ./login/deployment/emofy-db-container-deployment.yaml
kubectl create -f ./login/service/emofy-db-container-service.yaml

docker login
cd ./login
call mvn clean install
docker build -t emofy-login-service:latest .
cd ..
docker tag emofy-login-service:latest siralex01/emofy-login-service:latest
docker push siralex01/emofy-login-service:latest

kubectl create -f ./login/deployment/emofy-login-service-deployment.yaml
kubectl create -f ./login/service/emofy-login-service-service.yaml
