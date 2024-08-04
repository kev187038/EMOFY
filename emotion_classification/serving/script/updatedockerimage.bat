docker login
cd emotion_classification/serving
docker build -t emotion_classification:latest .
cd ../..
docker tag image_filter:latest siralex01/emotion_classification:latest
docker push siralex01/emotion_classification:latest
