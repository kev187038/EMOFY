FROM python:3.10-slim

WORKDIR /filters

COPY requirements.txt .
COPY Image.py .
COPY server.py .

RUN pip install -r requirements.txt

ENTRYPOINT ["python", "server.py"]