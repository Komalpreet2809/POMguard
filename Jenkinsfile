pipeline {
    agent any

    environment {
        IMAGE_NAME = 'pomguard'
        IMAGE_TAG  = "${env.BUILD_NUMBER}"
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn -B verify'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -t ${IMAGE_NAME}:latest ."
            }
        }

        stage('Smoke Test') {
            steps {
                sh '''
                    set -e
                    docker rm -f pomguard-smoke 2>/dev/null || true
                    docker run -d --name pomguard-smoke ${IMAGE_NAME}:${IMAGE_TAG}
                    echo "Waiting for app health..."
                    for i in $(seq 1 30); do
                        status=$(docker inspect -f '{{.State.Health.Status}}' pomguard-smoke 2>/dev/null || echo unknown)
                        echo "  attempt $i: $status"
                        if [ "$status" = "healthy" ]; then
                            echo "App is healthy"
                            exit 0
                        fi
                        sleep 2
                    done
                    echo "App failed to become healthy"
                    docker logs pomguard-smoke
                    exit 1
                '''
            }
            post {
                always {
                    sh 'docker rm -f pomguard-smoke 2>/dev/null || true'
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully'
        }
        failure {
            echo 'Pipeline failed'
        }
    }
}
