pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "silpaguna/dockerjan"
    }

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

        stage('Docker Build') {
            steps {
                sh '''
                  docker build -t $DOCKER_IMAGE:${BUILD_NUMBER} .
                  docker tag $DOCKER_IMAGE:${BUILD_NUMBER} $DOCKER_IMAGE:latest
                '''
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                      echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                      docker push $DOCKER_IMAGE:${BUILD_NUMBER}
                      docker push $DOCKER_IMAGE:latest
                    '''
                }
            }
        }
    }
}
