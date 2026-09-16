pipeline {
    agent any

    tools {
        jdk 'jdk21'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Build #${BUILD_NUMBER}"
                sh 'ls -la'
            }
        }

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

        stage('Docker Build (opcional)') {
            steps {
                echo 'Pendiente: configurar Docker CLI o agent docker'
                // sh 'docker build -t twitchspamdetector/moderation-engine:${BUILD_NUMBER} .'
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo "✅ Build #${BUILD_NUMBER} OK"
        }
        failure {
            echo "❌ Build #${BUILD_NUMBER} FALLÓ"
        }
    }
}