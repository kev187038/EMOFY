
kubectl delete -f ./deployment/emofy-db-container-deployment.yaml
kubectl delete -f ./service/emofy-db-container-service.yaml
kubectl delete -f ./deployment/emofy-login-service-deployment.yaml
kubectl delete -f ./service/emofy-login-service-service.yaml
kubectl delete -f ./deployment/emofy-db-container-claim0-persistentvolumeclaim.yaml
REM minikube delete
