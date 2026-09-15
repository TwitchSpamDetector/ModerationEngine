pipeline {
    agent any

    tools {
        jdk 'jdk21' // Configurar esta tool en Jenkins > Global Tool Configuration
    }

    stages {
        stage('Install') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw -B dependency:go-offline'
            }
        }

        stage('Test') {
            steps {
                sh './mvnw -B test'
            }
        }

        stage('Build') {
            steps {
                sh './mvnw -B clean package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t twitchspamdetector/moderation-engine:${BUILD_NUMBER} .'
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}