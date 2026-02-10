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
          sh 'aws sts get-caller-identity'
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
        stage('SonarQube Analysis') {
              steps {
              withSonarQubeEnv('sonar') {
              sh '''
              mvn sonar:sonar \
              -Dsonar.projectKey=dockerjan \
              -Dsonar.projectName=dockerjan
               '''
                }
               }
              }
        stage('Quality Gate') {
             steps {
               timeout(time: 5, unit: 'MINUTES') {
                waitForQualityGate abortPipeline: true
               }
               }
              }

             stage('Login to ECR') {
              steps {
                sh '''
                aws ecr get-login-password --region $AWS_REGION \
                | docker login --username AWS --password-stdin \
                $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com
                '''
                }
               }


       stage('Docker Build & Push') {
         steps {
              sh '''
              docker build -t dockerjan:${BUILD_NUMBER} .
              docker tag dockerjan:${BUILD_NUMBER}-${GIT_COMMIT[0..7] \
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
