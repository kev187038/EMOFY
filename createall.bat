minikube start

call ./login/createall.bat 
call ./image_storage/createall.bat
call ./image_filter/script/createall.bat
call ./emotion_classification/serving/script/createall.bat
call ./emotion_classification/retraining/script/createall.bat
call ./ElkStack/createall.bat

