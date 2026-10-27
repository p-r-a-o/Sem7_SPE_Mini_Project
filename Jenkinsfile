pipeline {
    agent any

    triggers {
        // GitHub webhook (Payload URL: http://<jenkins>/github-webhook/)
        githubPush()
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    environment {
        // TODO: replace with your values
        DOCKERHUB_REPO = 'pramatharao/spe_mp_calculator'

        // Jenkins credentials ID (Username with password) holding the Docker Hub login
        DOCKERHUB_CREDS = 'dockerhub-credentials'

        IMAGE_TAG            = "${env.BUILD_NUMBER}"
        COMPOSE_PROJECT_NAME = 'calculator'   // fixed name => re-runs update the same stack
        HOST_PORT            = '1111'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: false
                }
            }
        }

        stage('Docker Build') {
            steps {
                // Tagged with the build number; also tagged :latest and calculator:latest
                // (the image name docker-compose.yml runs) so Deploy uses exactly this build.
                sh '''
                    docker build \
                      -t "${DOCKERHUB_REPO}:${IMAGE_TAG}" \
                      -t "${DOCKERHUB_REPO}:latest" \
                      -t calculator:latest .
                '''
            }
        }

        stage('Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDS,
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_PASS')]) {
                    sh '''
                        echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin
                        docker push "${DOCKERHUB_REPO}:${IMAGE_TAG}"
                        docker push "${DOCKERHUB_REPO}:latest"
                    '''
                }
            }
            post {
                always {
                    sh 'docker logout || true'
                }
            }
        }

        stage('Deploy') {
            steps {
                // Idempotent: only recreates containers whose image/config changed.
                // --wait blocks until the compose health checks report healthy.
                sh 'docker compose up -d --no-build --remove-orphans --wait --wait-timeout 300'

                // Verify through the Nginx proxy
                sh '''
                    for i in $(seq 1 15); do
                      if curl -fsS "http://localhost:${HOST_PORT}/actuator/health" | grep -q '"status":"UP"'; then
                        echo "Health check passed"
                        exit 0
                      fi
                      echo "Waiting for service to be healthy ($i/15)..."
                      sleep 4
                    done
                    echo "Health check FAILED"
                    exit 1
                '''
            }
            post {
                failure {
                    sh 'docker compose ps || true; docker compose logs --tail=50 || true'
                }
            }
        }
    }

    post {
        success {
            mail to: "${env.EMAIL_TO}",
                 subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Build succeeded and was deployed.\n\nImage: ${env.DOCKERHUB_REPO}:${env.IMAGE_TAG}\nDetails: ${env.BUILD_URL}"
        }
        failure {
            mail to: "${env.EMAIL_TO}",
                 subject: "FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Build failed.\n\nCheck the console output: ${env.BUILD_URL}console"
        }
        unstable {
            mail to: "${env.EMAIL_TO}",
                 subject: "UNSTABLE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Build is unstable.\n\nDetails: ${env.BUILD_URL}"
        }
    }
}
