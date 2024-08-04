call emotion_classification\serving\script\updatedockerimage.bat

kubectl apply -f https://raw.githubusercontent.com/NVIDIA/k8s-device-plugin/v0.12.3/nvidia-device-plugin.yml
minikube addons enable nvidia-driver-installer
minikube addons enable nvidia-gpu-device-plugin
minikube addons enable nvidia-device-plugin


kubectl create -f ./emotion_classification/serving/k8/service-reader-role.yaml
kubectl create -f ./emotion_classification/serving/k8/service-reader-rolebinding.yaml
kubectl create -f ./emotion_classification/serving/k8/login-credentials.yaml
kubectl create -f ./emotion_classification/serving/k8/minio-credentials.yaml
kubectl create -f ./emotion_classification/serving/k8/minio-model.yaml
kubectl create -f ./emotion_classification/serving/k8/deployment.yaml
kubectl create -f ./emotion_classification/serving/k8/service.yaml