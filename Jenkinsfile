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

       stage('Docker Build & Push') {
         steps {
              sh '''
              docker build -t dockerjan:${BUILD_NUMBER} .
              docker tag dockerjan:${BUILD_NUMBER} \
              776751404462.dkr.ecr.ap-south-1.amazonaws.com/dockerjan:${BUILD_NUMBER}
              aws ecr get-login-password --region ap-south-1 | \
              docker login --username AWS --password-stdin 776751404462.dkr.ecr.ap-south-1.amazonaws.com
              docker push 776751404462.dkr.ecr.ap-south-1.amazonaws.com/dockerjan:${BUILD_NUMBER}
        '''
    }
}

        stage('Deploy to EKS') {
           steps {
                 sh '''
                helm upgrade --install dockerjan ./dockerjan-chart \
                --namespace default \
                --set image.repository=776751404462.dkr.ecr.ap-south-1.amazonaws.com/dockerjan \
                --set image.tag=${BUILD_NUMBER}
        '''
    }
}

    }
}
