kubectl delete -f ./emotion_classification/serving/k8/login-credentials.yaml
kubectl delete -f ./emotion_classification/serving/k8/minio-credentials.yaml
kubectl delete -f ./emotion_classification/serving/k8/minio-model.yaml
kubectl delete -f ./emotion_classification/serving/k8/deployment.yaml
kubectl delete -f ./emotion_classification/serving/k8/service.yaml

