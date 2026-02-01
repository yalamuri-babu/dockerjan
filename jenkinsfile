pipeline (
    agent any

    stages {

        stage('Sanity Check') {
            steps {
                echo 'Jenkins is alive'
                sh 'whoami'
                sh 'pwd'
            }
        }

        stage('AWS Identity Check') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'aws-jenkins'
                ]]) {
                    sh 'aws sts get-caller-identity'
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
    }
}
