rm ./emotion_classification/retraining/minio.py
rm ./emotion_classification/retraining/face_detector.py

kubectl delete -f ./emotion_classification/retraining/k8/retrain-cron.yaml