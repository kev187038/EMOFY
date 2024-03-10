pipeline {
    agent any
    stages {
        stage('users db setup') {
            steps {
                script {
                    dir('login') {
                        sh 'kubectl create -f deployment/emofy-db-container-claim0-persistentvolumeclaim.yaml'
                        sh 'kubectl create -f deployment/emofy-db-container-deployment.yaml'
                        sh 'kubectl create -f service/emofy-db-container-service.yaml'
                    }
                }
            }
        }

        stage('deploy login service') {
            steps {
                script {
                    dir('login') {
                        sh 'kubectl create -f deployment/emofy-login-service-deployment.yaml'
                        sh 'kubectl create -f service/emofy-login-service-service.yaml'
                        sleep 5
                        withEnv(['JENKINS_NODE_COOKIE=dontkillMe']) {
		            sh "nohup kubectl port-forward service/emofy-login-service 8085:8085 &"
		        }
                    }
                }
            }
        }
    }
}
