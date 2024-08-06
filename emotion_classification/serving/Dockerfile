FROM tensorflow/tensorflow:2.17.0

WORKDIR /emotion_classification

COPY utils/emotion_detector.py utils/emotion_detector.py
COPY utils/face_detector.py utils/face_detector.py
COPY utils/logger.py utils/logger.py
COPY utils/minio.py utils/minio.py

COPY requirements.txt .

COPY model.h5 . 
COPY server.py .

RUN apt-get update && apt-get install -y ffmpeg libsm6 libxext6 libgl1 && \
    pip install --upgrade pip && \
    pip install --ignore-installed blinker flask==3.0.2 && \
    pip install -r requirements.txt && \
    pip install nvidia-pyindex && \
    pip install nvidia-tensorrt

#CMD ["sh", "-c", "tail -f /dev/null"]
ENTRYPOINT ["python", "server.py"]
