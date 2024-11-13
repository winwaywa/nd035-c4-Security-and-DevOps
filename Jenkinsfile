pipeline {
    agent any
    stages {
        stage('Clone Repository') {
            steps {
                git 'https://github.com/winwaywa/nd035-c4-Security-and-DevOps.git'
            }
        }
        stage('Build') {
            steps {
                dir('starter_code') { // Chuyển vào thư mục starter_code
                   sh './mvnw clean package'
                }
            }
        }
        stage('Docker Build and Push') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'docker-hub-credentials') {
                        def appImage = docker.build("winwaywa/nd035-c4-security-and-devops")
                        appImage.push('latest')
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                sh '''
                docker stop spring_app || true
                docker rm spring_app || true
                docker run -d --name spring_app -p 8080:8080 winwaywa/nd035-c4-security-and-devops:latest
                '''
            }
        }
    }
}
