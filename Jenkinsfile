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
                sh 'docker-compose up -d'
            }
        }
    }
}
