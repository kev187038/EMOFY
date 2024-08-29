rm ./emotion_classification/retraining/minio_client.py
rm ./emotion_classification/retraining/face_detector.py
rm ./emotion_classification/retraining/logger.py

kubectl delete -f ./emotion_classification/retraining/k8/retrain-cron.yaml