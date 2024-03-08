pipeline {
    agent any
    stages {
        stage('mvn-build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('docker-compose') {
        
            steps {
            
                sh 'docker build -t emofy-login-service -f Dockerfile .'
                
                sh 'docker build -t emofy-db-container -f Dockerfile-db .'
            }
        }
        
        stage('Deploy to Kubernetes') {
            steps {
            
            	sh 'kubectl apply -f /deployment/emofy-db-container-deployment.yaml'
               
                sh 'kubectl apply -f /deployment/emofy-login-service-deployment.yaml'
            }
        }
    }
}
