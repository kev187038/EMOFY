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

        stage('Tunnel to localhost') {
            environment {
                time_to_sleep = 10
                tries = 20
            }
            steps {
                script {
                    is_pod_running = false                   
                    for (int i = 0; i < tries.toInteger() && !is_pod_running; i++) {                     
                        is_pod_running = sh(script: "kubectl get pod -l io.kompose.service=emofy-login-service -o jsonpath='{.items[*].status.phase}'", returnStdout: true).contains("Running")
                        is_pod_running = is_pod_running && sh(script: "kubectl get pod -l app=emofy-image-storage -o jsonpath='{.items[*].status.phase}'", returnStdout: true).contains("Running")

                        sleep time_to_sleep
                    }                   
                    if (is_pod_running) {
                        sleep time_to_sleep
                        withEnv(['JENKINS_NODE_COOKIE=dontkillMe']) {
                            sh 'nohup kubectl port-forward service/emofy-login-service 8085:8085 &'
                            sh 'nohup kubectl port-forward service/emofy-image-storage 8081:8081 &'
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
