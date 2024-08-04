call .\image_filter\script\updatedockerimage.bat

kubectl create -f ./image_filter/k8/deployment.yaml
kubectl create -f ./image_filter/k8/service.yaml
