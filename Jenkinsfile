pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Smoke Test') {
            steps {
                sh 'ls -la'
            }
        }

        stage('Maven Build') {
            steps {
                sh 'mvn -B clean package'
            }
        }
        post {
    success {
        archiveArtifacts artifacts: 'target/*.jar'
    }
}
    }
}
