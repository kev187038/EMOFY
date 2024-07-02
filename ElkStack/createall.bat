kubectl apply -f ./ElkStack/elasticsearch-deployment.yaml
kubectl apply -f ./ElkStack/kibana-deployment.yaml
kubectl apply -f ./ElkStack/logstash-deployment.yaml
kubectl apply -f ./ElkStack/elasticsearch-service.yaml
kubectl apply -f ./ElkStack/kibana-service.yaml
kubectl apply -f ./ElkStack/logstash-service.yaml
