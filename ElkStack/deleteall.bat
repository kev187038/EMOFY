kubectl delete -f ./ElkStack/elasticsearch-deployment.yaml
kubectl delete -f ./ElkStack/kibana-deployment.yaml
kubectl delete -f ./ElkStack/logstash-deployment.yaml
kubectl delete -f ./ElkStack/elasticsearch-service.yaml
kubectl delete -f ./ElkStack/kibana-service.yaml
kubectl delete -f ./ElkStack/logstash-service.yaml
