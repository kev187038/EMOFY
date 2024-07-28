call emotion_classification\script\updatedockerimage.bat

kubectl create -f ./serving/k8/login-credentials.yaml
kubectl create -f ./serving/k8/minio-credentials.yaml
kubectl create -f ./serving/k8/minio-model.yaml
kubectl create -f ./serving/k8/deployment.yaml
kubectl create -f ./serving/k8/service.yaml

kubectl create -f ./retraining/k8/retrain-cron.yaml