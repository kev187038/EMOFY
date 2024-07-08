kubectl create -f ./deployment/emofy-db-container-claim0-persistentvolumeclaim.yaml
kubectl create -f ./deployment/emofy-db-container-deployment.yaml
kubectl create -f ./service/emofy-db-container-service.yaml

call ./updatedockerimage.bat

kubectl create -f ./deployment/emofy-login-service-deployment.yaml
kubectl create -f ./service/emofy-login-service-service.yaml
