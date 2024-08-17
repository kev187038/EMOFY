call emotion_classification\retraining\script\updatedockerimage.bat

copy .\emotion_classification\serving\utils\minio.py .\emotion_classification\retraining\minio.py
copy .\emotion_classification\serving\utils\face_detector.py .\emotion_classification\retraining\face_detector.py


kubectl create -f ./emotion_classification/retraining/k8/retrain-cron.yaml