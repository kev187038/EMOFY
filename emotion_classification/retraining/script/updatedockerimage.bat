
cd emotion_classification/retraining
docker build -t emofy-retraining:latest .
cd ../..
docker tag emotion_classification_retraining:latest siralex01/emofy:latest
docker push siralex01/emofy:latest
