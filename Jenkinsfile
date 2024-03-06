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
                sh 'cp target/spring-scala-0.0.1-SNAPSHOT.jar .'
            }
        }
        stage('docker-build') {
            steps {
                sh 'docker build -t emofy-app .'
            }
        }
        stage('docker-run') {
            steps {
                sh 'docker run -d -p 8080:8085 emofy-app'
            }            

        }
    }
}
