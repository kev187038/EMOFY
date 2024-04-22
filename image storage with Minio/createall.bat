
kubectl create -f deployment/minio_pvc_deployment.yaml
kubectl create -f deployment/minio_deployment.yaml
kubectl create -f service/minio_service.yaml

kubectl create -f .\deployment\image_storage_deployment.yaml
kubectl create -f .\service\image_storage_service.yaml