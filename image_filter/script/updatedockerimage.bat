docker login
cd image_filter
docker build -t image_filter:latest .
cd ..
docker tag image_filter:latest siralex01/image_filter:latest
docker push siralex01/image_filter:latest
