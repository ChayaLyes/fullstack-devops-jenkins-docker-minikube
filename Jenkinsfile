pipeline {
    agent any

    tools {
        maven 'maven-3.9'
    }

    environment {
        APP_NAME          = 'java-maven-app'
        DOCKER_HUB_USERNAME = 'chayalyes'
        DOCKER_REPO       = "${DOCKER_HUB_USERNAME}/${APP_NAME}"
        DOCKER_REPO_SERVER = 'docker.io'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code from GitHub...'
                checkout scm
            }
        }

        stage('Increment Version') {
            steps {
                script {
                    echo 'Incrementing app version...'
                    sh '''
                        mvn build-helper:parse-version versions:set \
                        -DnewVersion=\\${parsedVersion.majorVersion}.\\${parsedVersion.minorVersion}.\\${parsedVersion.nextIncrementalVersion} \
                        versions:commit
                    '''
                    // mvn help:evaluate est plus fiable qu'un regex sur pom.xml
                    def version = sh(
                        script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout",
                        returnStdout: true
                    ).trim()
                    env.IMAGE_NAME = "${version}-${BUILD_NUMBER}"
                    echo "New version: ${env.IMAGE_NAME}"
                }
            }
        }

        stage('Build Application') {
            steps {
                echo 'Building Maven application (skip tests — next stage)...'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Run Tests') {
            steps {
                echo 'Running tests...'
                sh 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    echo 'Building Docker image...'
                    sh "docker build -t ${DOCKER_REPO}:${IMAGE_NAME} ."
                    sh "docker tag  ${DOCKER_REPO}:${IMAGE_NAME} ${DOCKER_REPO}:latest"
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    echo 'Pushing image to Docker Hub...'
                    withCredentials([usernamePassword(
                        credentialsId: 'docker-hub-credentials',
                        usernameVariable: 'USER',
                        passwordVariable: 'PASS'
                    )]) {
                        sh 'echo $PASS | docker login -u $USER --password-stdin ${DOCKER_REPO_SERVER}'
                        sh "docker push ${DOCKER_REPO}:${IMAGE_NAME}"
                        sh "docker push ${DOCKER_REPO}:latest"
                    }
                }
            }
        }

        stage('Deploy to Minikube') {
            steps {
                script {
                    echo 'Deploying application to Minikube...'
                    sh """
                        export DOCKER_REPO=${DOCKER_REPO}
                        export IMAGE_NAME=${IMAGE_NAME}
                        envsubst < kubernetes/deployment.yaml | kubectl apply -f -
                    """
                }
            }
        }

        stage('Deploy Monitoring') {
            steps {
                script {
                    echo 'Deploying Prometheus & Grafana...'
                    sh 'kubectl apply -f kubernetes/monitoring/'
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                script {
                    echo 'Verifying deployment...'
                    sh 'kubectl get deployments'
                    sh 'kubectl get pods'
                    sh 'kubectl get services'
                    sh 'kubectl get pods -n monitoring'
                    sh 'kubectl wait --for=condition=ready pod -l app=java-maven-app --timeout=300s || true'
                }
            }
        }

        stage('Commit Version Update') {
            steps {
                script {
                    echo 'Committing version bump...'
                    withCredentials([usernamePassword(
                        credentialsId: 'github-credentials',
                        usernameVariable: 'USER',
                        passwordVariable: 'PASS'
                    )]) {
                        sh """
                            git config user.name  "Jenkins CI"
                            git config user.email "jenkins@ci.local"
                            git add pom.xml
                            git commit -m "ci: version bump to ${env.IMAGE_NAME} [skip ci]" || echo "No changes to commit"
                            git push https://${USER}:${PASS}@github.com/chayalyes/fullstack-devops-jenkins-docker-minikube.git HEAD:main || echo "Push failed"
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline completed — ${env.IMAGE_NAME} deployed"
            echo "Prometheus : http://<minikube-ip>:30090"
            echo "Grafana    : http://<minikube-ip>:30030  (admin / admin123)"
        }
        failure {
            echo 'Pipeline failed!'
        }
        always {
            cleanWs()
        }
    }
}
