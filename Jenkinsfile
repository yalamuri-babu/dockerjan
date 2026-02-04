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
     environment {
    AWS_REGION = "ap-south-1"
    ECR_REPO   = "dockerjan"
    AWS_ACCOUNT_ID = "776751404462"
    IMAGE_TAG = "${BUILD_NUMBER}"
}

stage('Docker Build') {
    steps {
        sh '''
          docker build -t $ECR_REPO:$IMAGE_TAG .
        '''
    }
}

stage('Login to ECR') {
    steps {
        withCredentials([[
            $class: 'AmazonWebServicesCredentialsBinding',
            credentialsId: 'awsecr-jenkins'
        ]]) {
            sh '''
              aws ecr get-login-password --region $AWS_REGION \
              | docker login --username AWS --password-stdin \
              $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com
            '''
        }
    }
}

stage('Tag & Push to ECR') {
    steps {
        sh '''
          docker tag $ECR_REPO:$IMAGE_TAG \
          $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPO:$IMAGE_TAG

          docker push \
          $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPO:$IMAGE_TAG
        '''
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
