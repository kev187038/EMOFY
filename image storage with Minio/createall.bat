
kubectl create -f deployment/minio_pvc_deployment.yaml
kubectl create -f deployment/minio_deployment.yaml
kubectl create -f service/minio_service.yaml