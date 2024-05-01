
REM kubectl delete -f ./image_storage/deployment/minio_deployment.yaml
REM kubectl delete -f ./image_storage/service/minio_service.yaml
REM kubectl delete -f ./image_storage/deployment/minio_pvc_deployment.yaml
kubectl delete -f ./image_storage/deployment/image_storage_deployment.yaml
kubectl delete -f ./image_storage/service/image_storage_service.yaml
