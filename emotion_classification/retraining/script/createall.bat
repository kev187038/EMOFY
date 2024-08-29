copy .\emotion_classification\serving\utils\minio_client.py .\emotion_classification\retraining\minio_client.py
copy .\emotion_classification\serving\utils\face_detector.py .\emotion_classification\retraining\face_detector.py
copy .\emotion_classification\serving\utils\logger.py .\emotion_classification\retraining\logger.py

call emotion_classification\retraining\script\updatedockerimage.bat

kubectl create -f ./emotion_classification/retraining/k8/retrain-cron.yaml