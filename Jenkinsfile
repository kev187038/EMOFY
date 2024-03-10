pipeline {
    agent any
    stages {
        stage('users db setup') {
            dir('login'){
                steps {
                    sh 'kubectl create -f deployment/emofy-db-container-claim0-persistentvolumeclaim.yaml'
                    sh 'kubectl create -f deployment/emofy-db-container-deployment.yaml'
                    sh 'kubectl create -f services/emofy-db-container-service.yaml'
                }
            }
        }

        stage('deploy login service') {
        
             dir('login'){
                steps {
                    sh 'kubectl create -f deployment/emofy-login-service-deployment.yaml'
                    sh 'kubectl create -f services/emofy-login-service-service.yaml'
                }
            }
        }
        
    }
}
