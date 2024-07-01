kubectl create -f ./login/deployment/emofy-db-container-claim0-persistentvolumeclaim.yaml
kubectl create -f ./login/deployment/emofy-db-container-deployment.yaml
kubectl create -f ./login/service/emofy-db-container-service.yaml

call ./login/updatedockerimage.bat

kubectl create -f ./login/deployment/emofy-login-service-deployment.yaml
kubectl create -f ./login/service/emofy-login-service-service.yaml
