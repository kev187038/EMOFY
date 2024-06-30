
kubectl create -f ./image_storage/deployment/minio_pvc_deployment.yaml
kubectl create -f ./image_storage/deployment/minio_deployment.yaml
kubectl create -f ./image_storage/service/minio_service.yaml

./image_storage/updatedockerimage.bat