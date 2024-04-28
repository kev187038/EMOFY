REM mvn clean install
docker login
docker build -t emofy-image-storage:latest .
docker tag emofy-image-storage:latest siralex01/emofy-image-storage:latest
docker push siralex01/emofy-image-storage:latest
kubectl create -f ./deployment/image_storage_deployment.yaml
kubectl create -f ./service/image_storage_service.yaml
