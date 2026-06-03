# EMOFY — Emotion Detection Platform

**Authors:** Alessio Maiola, Gabriele Matini, Placido Pellegriti

---

A cloud-native emotion detection platform built on a **polyglot microservice architecture**. Users upload images via a drag-and-drop web UI, apply visual filters, and run real-time facial emotion classification (angry, disgust, fear, happy, neutral, sad, surprise) powered by a deep learning model that is continuously retrained.

A key aspect of the platform is its **human-in-the-loop design**: every image uploaded by users can contribute to the training dataset. After the model predicts an emotion, the user can manually correct the label — that corrected label is persisted and later consumed by the retraining pipeline. This means the community of users directly participates in dataset creation, enabling the model to progressively improve over time on real-world data without manual curation.

---

## High-Level Architecture

The system is composed of five independently deployable microservices, a web frontend, and an observability stack. Everything runs inside a Kubernetes cluster and is delivered through Jenkins CI/CD.

Services communicate over **REST/JSON**. Authentication is centralized in the Login Service, which issues JWTs as HTTP-only cookies validated by all downstream services. The typical user flow is:

1. User authenticates → receives JWT
2. User uploads an image → face detection validates a face is present → image is stored in MinIO
3. Emotion classification returns a prediction → user may correct the label
4. On a schedule, the retraining CronJob fetches newly labeled images, incrementally retrains the model, and uploads new weights to MinIO
5. The serving service picks up the updated model on next request cycle

| Service | Language | Port | Role |
|---|---|---|---|
| **Login** | Java 17 / Spring Boot | 8085 | Authentication (JWT + Google OAuth2), user management |
| **Image Storage** | Java 17 / Spring Boot | 8081 | Image CRUD via MinIO, serves the web frontend |
| **Image Filter** | Python 3.10 / Flask | 5000 | 16 image filters (OpenCV) |
| **Emotion Classification** | Python 3.10 / Flask + TensorFlow | 5050 | Face detection and emotion inference |
| **Retraining** | Python 3.10 / TensorFlow | — | Scheduled model retraining (K8s CronJob) |
| **ELK Stack** | Elasticsearch, Logstash, Kibana | 9200, 5044, 5601 | Centralized structured logging |

### Frontend
The Image Storage service serves a **server-rendered web UI** (Thymeleaf + vanilla JavaScript) that provides:
- Drag-and-drop image upload with automatic face validation
- Sidebar image gallery sorted by timestamp
- Filter panel with 16 OpenCV filters (gray, blur, sepia, cartoonify, emboss, sharpen, rainbowify, mirror, etc.)
- Real-time emotion prediction display with manual label correction
- Image deletion

---

## Design Choices

### Polyglot Microservices
Each service uses the language and framework best suited to its domain:
- **Java / Spring Boot** for the transactional backend services that benefit from strong typing, mature ORM (JPA), and the Spring Security ecosystem (OAuth2, CSRF, JWT).
- **Python / Flask** for ML-adjacent services where TensorFlow, Keras, and OpenCV are first-class citizens and rapid prototyping matters.

This avoids forcing a single runtime to cover both enterprise auth flows and GPU-accelerated inference.

### Authentication & Security
- Centralized OAuth2 + JWT flow: the Login Service issues signed tokens (24-hour expiration) as HTTP-only secure cookies, and all other services validate them independently — no shared session state.
- Google OAuth2 integration for social login.
- Spring Security handles CSRF protection and role-based access on the Java side.
- PostgreSQL stores credentials and user profiles with Spring Data JPA.

### Storage Layer
- **MinIO** provides S3-compatible object storage for both user images (bucket `emofy-images`) and trained model weights (bucket `models`). Images are organized by user ID with metadata (filename, label, timestamp). This decouples the ML pipeline from the serving layer — the retraining job writes a new model to MinIO, and the serving service pulls it without downtime.
- **PostgreSQL** is used exclusively for relational user data, keeping concerns separated.
- Upload limit: 10 MB per image.

### ML Pipeline
- The serving service loads a Keras `.keras` model into memory and exposes `/detect_face` and `/detect_emotion` endpoints. It automatically fetches the latest model from MinIO and reloads on changes. Supports 7 emotion classes: angry, disgust, fear, happy, neutral, sad, surprise.
- GPU inference is supported via NVIDIA CUDA / TensorRT when available.
- The retraining service runs as a **Kubernetes CronJob**. It authenticates against the Login Service, fetches newly labeled images from the Image Storage API, filters for valid faces, and performs incremental training (10 epochs, Adam optimizer, categorical cross-entropy). Updated weights are saved with a timestamp (`model_YYYYMMDDHHmmss.keras`) and uploaded to MinIO.
- This **user-driven feedback loop** is central to the project: the more users interact with the platform and correct labels, the larger and more diverse the training dataset becomes, directly improving classification accuracy.
- Retraining never blocks inference — the two services are fully decoupled.

### Observability
- Spring Boot services emit structured JSON logs via the Logstash Logback Encoder.
- Logstash ingests logs over TCP (port 5044) and forwards them to Elasticsearch.
- Kibana provides dashboards and search across all services.
- The entire ELK stack is deployable via Docker Compose (local dev) or Kubernetes manifests (production-like).

### Containerization & Orchestration
- Every service has its own `Dockerfile` built on minimal base images (OpenJDK 17, Python 3.10-slim, TensorFlow GPU).
- Docker images are published to Docker Hub under the `siralex01` namespace.
- Kubernetes manifests define Deployments, Services, PersistentVolumeClaims, ConfigMaps, Secrets, and RBAC policies (ServiceAccount, Role, RoleBinding for in-cluster K8s API access).
- The cluster runs on **Minikube** for local development, with optional NVIDIA GPU device plugin support for ML workloads.

---

## Running the Platform

### Prerequisites
- Docker
- Minikube
- kubectl
- Maven (for building Java services locally)
- Python 3.10+ (for running Python services locally)

### Quick Start (Kubernetes)

**Linux / macOS:**
```bash
minikube start
./createall.sh
```

**Windows:**
```batch
minikube start --driver=docker --gpus all
createall.bat
```

This starts Minikube and deploys all services in dependency order:
1. Login Service + PostgreSQL (with PVC)
2. Image Storage + MinIO (with PVC)
3. Image Filter
4. Emotion Classification (Serving) + RBAC + Secrets
5. Retraining CronJob
6. ELK Stack

### Accessing Services

After deployment, use `kubectl port-forward` to expose services locally:

```bash
kubectl port-forward service/emofy-login-service 8085:8085
kubectl port-forward service/emofy-image-storage 8081:8081
kubectl port-forward service/image-filters-service 5000:5000
kubectl port-forward service/emotion-detector-service 5050:5050
kubectl port-forward service/minio-console 9001:9001
kubectl port-forward service/kibana 5601:5601
```

| URL | Service |
|---|---|
| http://localhost:8085 | Login / Registration |
| http://localhost:8081 | Main App (Image Upload, Filters, Emotion Detection) |
| http://localhost:9001 | MinIO Console |
| http://localhost:5601 | Kibana Dashboards |

### Tear Down

```bash
./deleteall.sh    # or deleteall.bat on Windows
```

This removes all Kubernetes resources and deletes the Minikube cluster.

### Local Development with Docker Compose

The Login Service and ELK Stack can also be run standalone via Docker Compose for local development:

```bash
cd login && docker-compose up        # Login + PostgreSQL
cd ElkStack && docker-compose up     # Elasticsearch + Logstash + Kibana
```

### Updating Docker Images

Each service has an `updatedockerimage.bat` script that builds, tags, and pushes the image to Docker Hub:

```batch
cd image_filter && script\updatedockerimage.bat
```

---

## Jenkins CI/CD

The project includes **declarative Jenkins pipelines** for fully automated deployment:

- `Jenkinsfile` — Linux/Unix pipeline
- `Jenkinsfile-windows` — Windows pipeline
- `translator.py` — Converts the Windows Jenkinsfile to Linux format (path separators, shell commands, backgrounding syntax)

### Pipeline Parameters

| Parameter | Default | Description |
|---|---|---|
| `USE_GPU` | `false` | Enables NVIDIA GPU support in Minikube (`--gpus all`, GPU device plugin addons) |

### Pipeline Stages

1. **Start Minikube** — Initializes a single-node cluster. When `USE_GPU` is true, installs NVIDIA driver, GPU device plugin, and device plugin addons.
2. **Deploy Login Service** — PVC + PostgreSQL deployment/service + Login deployment/service
3. **Deploy Image Storage** — PVC + MinIO deployment/service + Image Storage deployment/service
4. **Deploy Image Filter** — Deployment + Service
5. **Deploy ELK Stack** — Elasticsearch, Logstash, Kibana deployments + services
6. **Deploy Emotion Classification** — Deployment + Service + Secrets (login creds, MinIO creds, model bucket) + RBAC (Role + RoleBinding)
7. **Deploy Retraining CronJob** — Scheduled retraining job
8. **Tunnel to Localhost** — Waits for all pods to reach `Running` state (up to 100 retries × 30s), then establishes `kubectl port-forward` for all services

---

## Technologies

| Category | Stack |
|---|---|
| **Languages** | Java 17, Python 3.10, JavaScript (vanilla) |
| **Backend** | Spring Boot 3.x (Web, Data JPA, Security), Flask (Waitress WSGI) |
| **Frontend** | Thymeleaf (server-side rendering), vanilla JS, HTML/CSS |
| **Machine Learning** | TensorFlow 2.17, Keras, OpenCV 4.9, NumPy, Pillow |
| **Data Collection** | BeautifulSoup 4 (web scraping), user-contributed labeled uploads |
| **Databases** | PostgreSQL 13 |
| **Object Storage** | MinIO (S3-compatible) |
| **Containers** | Docker (OpenJDK 17, Python 3.10-slim, TensorFlow GPU base images) |
| **Orchestration** | Kubernetes — Deployments, Services, CronJobs, PVCs, RBAC, ConfigMaps, Secrets |
| **CI/CD** | Jenkins (Declarative Pipelines, multi-OS, parameterized GPU support) |
| **Logging** | Elasticsearch 8.14, Logstash 8.14, Kibana 8.14, Logback JSON Encoder |
| **Auth** | OAuth2, Google OAuth2, JWT (24h expiry), Spring Security, CSRF |
| **API Style** | REST / JSON, OpenAPI / Swagger |
| **GPU Support** | NVIDIA CUDA, TensorRT (optional) |
| **Build Tools** | Maven, pip, Docker