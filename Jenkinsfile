pipeline {
    agent any
    stages {
        stage('Start minikube') {
            environment {
                nodes = 1
            }
            steps {
                script {
                    sh "minikube start --nodes=${nodes}"
                }
            }
        }

        stage('deploy login service') {
            steps {
                script {
                    dir('login') {
                        sh 'kubectl create -f deployment/emofy-db-container-claim0-persistentvolumeclaim.yaml'
                        sh 'kubectl create -f deployment/emofy-db-container-deployment.yaml'
                        sh 'kubectl create -f service/emofy-db-container-service.yaml'
                        sh 'kubectl create -f deployment/emofy-login-service-deployment.yaml'
                        sh 'kubectl create -f service/emofy-login-service-service.yaml'
                    }
                }
            }
        }

        stage('deploy image storage') {
            steps {
                script {
                    dir('image_storage') {
                        sh 'kubectl create -f deployment/minio_pvc_deployment.yaml'
                        sh 'kubectl create -f deployment/minio_deployment.yaml'
                        sh 'kubectl create -f service/minio_service.yaml'
                        sh 'kubectl create -f deployment/image_storage_deployment.yaml'
                        sh 'kubectl create -f service/image_storage_service.yaml'
                    }
                }
            }
        }

        stage('deploy image filter service')
        {
            steps {
                script {
                    dir('image_filter') {
                        sh 'kubectl create -f k8/deployment.yaml'
                        sh 'kubectl create -f k8/service.yaml'
                    }
                }
            }
        }

        stage('deploy model serving service')
        {
            steps {
                script {
                    dir('emotion_classification/serving') {
                        sh 'kubectl create -f k8/login-credentials.yaml'
                        sh 'kubectl create -f k8/minio-credentials.yaml'
                        sh 'kubectl create -f k8/minio-model.yaml'
                        sh 'kubectl create -f k8/deployment.yaml'
                        sh 'kubectl create -f k8/service.yaml'
                    }
                }
            }
        }

        stage('deploy retraining cronjob') {
            steps {
                script {
                    dir('emotion_classification/retraining') {
                        sh 'kubectl create -f k8/retrain-cron.yaml'
                    }
                }
            }
        }

        stage('deploy elk-stack')
        {
            steps {
                script {
                    dir('ElkStack') {
                        sh 'kubectl create -f elasticsearch-deployment.yaml'
                        sh 'kubectl create -f kibana-deployment.yaml'
                        sh 'kubectl create -f logstash-deployment.yaml'
                        sh 'kubectl create -f elasticsearch-service.yaml'
                        sh 'kubectl create -f kibana-service.yaml'
                        sh 'kubectl create -f logstash-service.yaml'
                    }
                }
            }
        }

        stage('Tunnel to localhost') {
            environment {
                time_to_sleep = 30
                tries = 100
            }
            steps {
                script {
                    is_pod_running = false                   
                    for (int i = 0; i < tries.toInteger() && !is_pod_running; i++) {                     
                        is_pod_running = sh(script: "kubectl get pod -l io.kompose.service=emofy-login-service -o jsonpath='{.items[*].status.phase}'", returnStdout: true).contains("Running")
                        is_pod_running = is_pod_running && sh(script: "kubectl get pod -l app=emofy-image-storage -o jsonpath='{.items[*].status.phase}'", returnStdout: true).contains("Running")
                        is_pod_running = is_pod_running && sh(script: "kubectl get pod -l app=image-filters -o jsonpath='{.items[*].status.phase}'", returnStdout: true).contains("Running")
                        is_pod_running = is_pod_running && sh(script: "kubectl get pod -l app=emotion-detector -o jsonpath='{.items[*].status.phase}'", returnStdout: true).contains("Running")
                        is_pod_running = is_pod_running && sh(script: "kubectl get pod -l app=minio -o jsonpath='{.items[*].status.phase}'", returnStdout: true).contains("Running")
                        is_pod_running = is_pod_running && sh(script: "kubectl get pod -l app=kibana -o jsonpath='{.items[*].status.phase}'", returnStdout: true).contains("Running")

                        sleep time_to_sleep
                    }                   
                    if (is_pod_running) {
                        sleep time_to_sleep
                        withEnv(['JENKINS_NODE_COOKIE=dontkillMe']) {
                            sh 'nohup kubectl port-forward service/emofy-login-service 8085:8085 &'
                            sh 'nohup kubectl port-forward service/emofy-image-storage 8081:8081 &'
                            sh 'nohup kubectl port-forward service/image-filters-service 5000:5000 &'
                            sh 'nohup kubectl port-forward service/emotion-detector-serivce 5050:5050 &'
                            sh 'nohup kubectl port-forward service/minio-console 9001:9001 &'
                            sh 'nohup kubectl port-forward service/kibana 5601:5601 &'
		                }
                    } 
                    else {
                        error "Pod not running after ${tries} tries."
                    }
                }
            }
        }
    }
}
