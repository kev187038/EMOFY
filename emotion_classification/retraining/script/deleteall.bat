del .\emotion_classification\retraining\minio.py
del .\emotion_classification\retraining\face_detector.py

kubectl delete -f ./emotion_classification/retraining/k8/retrain-cron.yaml


