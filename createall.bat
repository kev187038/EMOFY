@echo off

REM Check if an NVIDIA GPU is present
nvidia-smi >nul 2>&1

REM If the previous command was successful, start minikube with GPU support
if %ERRORLEVEL% == 0 (
    echo NVIDIA GPU detected, starting minikube with GPU support...
    minikube start --driver=docker --gpus all
) else (
    echo No NVIDIA GPU detected, starting minikube without GPU support (retraining unavailable)...
    minikube start --driver=docker
)

REM Run the remaining scripts
call ./login/createall.bat 
call ./image_storage/createall.bat
call ./image_filter/script/createall.bat
call ./emotion_classification/serving/script/createall.bat
call ./emotion_classification/retraining/script/createall.bat
call ./ElkStack/createall.bat
