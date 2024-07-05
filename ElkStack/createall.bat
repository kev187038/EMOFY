kubectl create -f ./ElkStack/elasticsearch-deployment.yaml
kubectl create -f ./ElkStack/kibana-deployment.yaml
kubectl create -f ./ElkStack/logstash-deployment.yaml
kubectl create -f ./ElkStack/elasticsearch-service.yaml
kubectl create -f ./ElkStack/kibana-service.yaml
kubectl create -f ./ElkStack/logstash-service.yaml
