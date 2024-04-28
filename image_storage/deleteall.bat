
kubectl delete -f ./deployment/minio_deployment.yaml
kubectl delete -f ./service/minio_service.yaml
kubectl delete -f ./deployment/minio_pvc_deployment.yaml
kubectl delete -f ./deployment/image_storage_deployment.yaml
kubectl delete -f ./service/image_storage_service.yaml
