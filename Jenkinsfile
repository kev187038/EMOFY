pipeline {
    agent any
    stages {
        stage('mvn-build') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('copy') {
            steps {
                sh 'cp target/emofy-0.0.1-SNAPSHOT.jar .'
            }
        }
        stage('docker-build') {
            steps {
                sh 'docker build -t emofy-app .'
            }
        }
        stage('docker-run') {
            steps {
                sh 'docker run -d -p 8085:8080 emofy-app'
            }            

        }
    }
}
