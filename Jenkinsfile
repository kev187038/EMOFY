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
                        sleep 10
		        sh "JENKINS_NODE_COOKIE=dontKillMe nohup kubectl port-forward emofy-login-service 8085:8085 &"
		        
                    }
                }
            }
        }
    }
}
