pipeline {
    agent any
    stages {
        stage('mvn-build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('copy') {
            steps {
                sh 'cp target/emofy-0.0.1-SNAPSHOT.jar .'
            }
        }
        stage('docker-compose') {
            steps {
                sh 'docker-compose up'
            }
        }
    }
}
