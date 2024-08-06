FROM tensorflow/tensorflow:2.17.0-gpu

WORKDIR /retraining

COPY requirements.txt .
COPY retraining.py .
COPY image_retriever.py .
COPY face_detector.py .
COPY minio.py .


RUN apt-get update && apt-get install -y ffmpeg libsm6 libxext6 libgl1 && \
    pip install --upgrade pip && \
    pip install --ignore-installed blinker flask==3.0.2 && \
    pip install -r requirements.txt && \
    pip install nvidia-pyindex && \
    pip install nvidia-tensorrt
    
RUN pip install -r requirements.txt

#CMD ["sh", "-c", "tail -f /dev/null"]
ENTRYPOINT ["python", "retraining.py"]