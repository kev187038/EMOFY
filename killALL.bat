
kubectl delete -f .\login\deployment\emofy-db-container-deployment.yaml
kubectl delete -f .\login\service\emofy-db-container-service.yaml
kubectl delete -f .\login\deployment\emofy-login-service-deployment.yaml
kubectl delete -f .\login\service\emofy-login-service-service.yaml
kubectl delete -f .\login\deployment\emofy-db-container-claim0-persistentvolumeclaim.yaml
