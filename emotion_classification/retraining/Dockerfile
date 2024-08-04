FROM python:3.10-slim

WORKDIR /filters

COPY requirements.txt .
COPY retraining.py .
COPY image_retriever.py .
COPY face_detector.py .
COPY minio.py .


RUN apt-get update && apt-get install ffmpeg libsm6 libxext6  -y
RUN pip install -r requirements.txt

ENTRYPOINT ["python", "retraining.py"]