docker login
cd image_filter
docker build -t image_filter:latest .
cd ..
docker tag image_filter:latest placidop/image_filter:latest
docker push placidop/image_filter:latest
