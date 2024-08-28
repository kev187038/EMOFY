from kubernetes import client, config
config.load_incluster_config()
from minio import Minio
import os

class MinioClient:
    def init_client(self):
        v1 = client.CoreV1Api()
        service = v1.read_namespaced_service(name=self.service_name, namespace='default')
        minio_service = f"{service.spec.cluster_ip}:{service.spec.ports[0].port}"

        self.minio_client = Minio(
            minio_service,  # Replace with your MinIO server URL
            access_key=os.getenv('MINIO_ACCESS_KEY'),  # Replace with your access key
            secret_key=os.getenv('MINIO_SECRET_KEY'),  # Replace with your secret key
            secure=False  # Set to False if your MinIO server is not using HTTPS
        )

    def init_bucket(self, name:str):
        if not self.minio_client.bucket_exists(name): minio_client.make_bucket(name)

    def download_file(self, bucket:str, minio_filename:str, path:str=None):
        if not path: path = minio_filename
        self.minio_client.fget_object(bucket, minio_filename, path)

    def upload_file(self, bucket:str, minio_filename:str, path:str):
        if not minio_filename: minio_filename = os.path.basename(path)
        self.minio_client.fput_object(bucket, minio_filename, path)

    def list_files(self, bucket:str):
        return self.minio_client.list_objects(bucket)

    def __init__(self, service_name:str='minio-service'):
        self.service_name = service_name
        self.init_client()



    
