cp ./emotion_classification/serving/utils/minio.py ./emotion_classification/retraining/minio_client.py
cp ./emotion_classification/serving/utils/face_detector.py ./emotion_classification/retraining/face_detector.py
cp ./emotion_classification/serving/utils/logger.py ./emotion_classification/retraining/logger.py

kubectl create -f ./emotion_classification/retraining/k8/retrain-cron.yaml