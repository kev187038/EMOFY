docker login
cd emotion_classification/serving
docker build -t emotion_classification:latest .
cd ../..
docker tag image_filter:latest siralex01/image_filter:latest
docker push siralex01/image_filter:latest

cd emotion_classification/retraining
docker build -t emotion_classification_retraining:latest .
cd ../..
docker tag emotion_classification_retraining:latest siralex01/emotion_classification_retraining:latest
docker push siralex01/emotion_classification_retraining:latest
