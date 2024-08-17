docker login
cd emotion_classification/retraining
docker build -t emofy-retraining:latest .
cd ../..
docker tag emofy-retraining:latest siralex01/emofy-retraining:latest
docker push siralex01/emofy-retraining:latest
