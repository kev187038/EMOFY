docker login
cd emotion_classification/serving
docker build -t emotion-detector:latest .
cd ../..
docker tag emotion-detector:latest siralex01/emotion-detector:latest
docker push siralex01/emotion-detector:latest
