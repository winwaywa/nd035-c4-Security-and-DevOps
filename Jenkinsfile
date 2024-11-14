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
                   sh 'chmod +x mvnw'
                   sh './mvnw clean package'
                }
            }
        }
        stage('Docker Build and Push') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', '2a5add46-c3fe-4fbe-b6f4-c912f1ef4981') {
                        def appImage = docker.build("hiimhiep/nd035-c4-security-and-devops")
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
                docker run -d --name spring_app -p 8080:8080 hiimhiep/nd035-c4-security-and-devops:latest
                '''
            }
        }
    }
}
