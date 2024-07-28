FROM python:3.10-slim

WORKDIR /filters

COPY requirements.txt .
COPY emotion_detector.py .
COPY face_detector.py .
COPY server.py .
# Per ora il modello viene copiato.
COPY model.h5 . 

COPY server.py .
COPY logger.py .

RUN apt-get update && apt-get install ffmpeg libsm6 libxext6  -y
RUN pip install -r requirements.txt

ENTRYPOINT ["python", "server.py"]