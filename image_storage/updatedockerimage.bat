docker login
cd image_storage
call mvn clean install
docker build -t emofy-image-storage:latest .
cd ..
docker tag emofy-image-storage:latest siralex01/emofy-image-storage:latest
docker push siralex01/emofy-image-storage:latest
