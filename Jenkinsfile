pipeline {

    agent any

    environment {
        AWS_REGION = "ap-south-1"
        ECR_REPO   = "dockerjan"
        AWS_ACCOUNT_ID = "776751404462"
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {

        stage('Sanity Check') {
            steps {
                echo "Jenkins is alive"
                sh 'whoami'
                sh 'pwd'
            }
        }

        stage('AWS Identity Check') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'awsecr-jenkins'
                ]]) {
                    sh '''
                      export AWS_DEFAULT_REGION=ap-south-1
                      aws sts get-caller-identity
                    '''
                }
            }
        }

        stage('Git Checkout') {
            steps {
                checkout scm
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
                  docker build -t $ECR_REPO:$IMAGE_TAG .
                '''
            }
        }

        stage('Login to ECR') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'aws-jenkins'
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
    }
}
