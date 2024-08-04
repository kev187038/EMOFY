call ./login/deleteall.bat
call ./image_storage/deleteall.bat
call ./image_filter/script/deleteall.bat
call ./emotion_classification/serving/script/deleteall.bat
call ./emotion_classification/retraining/script/deleteall.bat

call ./ElkStack/deleteall.bat

minikube delete
