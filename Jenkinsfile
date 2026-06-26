pipeline {
    agent any

    environment {
        DOCKERHUB_CREDS = credentials('dockerhub-credentials')
        SONAR_TOKEN     = credentials('sonar-token')
        VPS_HOST        = credentials('vps-host')
        IMAGE_NAME      = "${DOCKERHUB_CREDS_USR}/data-analist"
        IMAGE_TAG       = "${BUILD_NUMBER}"
        VPS_DEPLOY_PATH = '/opt/dataanalist'
    }

    tools {
        maven 'Maven-3.9'
        jdk   'JDK-21'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'java -version && mvn -version'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -B'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test -B'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: '**/target/site/jacoco/jacoco.xml']]
                    )
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests -B'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=data-analist -Dsonar.token=$SONAR_TOKEN'
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

        stage('Docker Build') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -t ${IMAGE_NAME}:latest ."
            }
        }

        stage('Docker Push') {
            steps {
                sh """
                    echo "${DOCKERHUB_CREDS_PSW}" | docker login -u "${DOCKERHUB_CREDS_USR}" --password-stdin
                    docker push ${IMAGE_NAME}:${IMAGE_TAG}
                    docker push ${IMAGE_NAME}:latest
                    docker logout
                """
            }
        }

        stage('Deploy to VPS') {
            when {
                branch 'main'
            }
            steps {
                sshagent(credentials: ['vps-ssh-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${VPS_HOST} '
                            cd ${VPS_DEPLOY_PATH} &&
                            IMAGE_TAG=${IMAGE_TAG} docker compose pull app &&
                            IMAGE_TAG=${IMAGE_TAG} docker compose up -d app
                        '
                    """
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo "Pipeline completed successfully — image: ${IMAGE_NAME}:${IMAGE_TAG}"
        }
        failure {
            echo "Pipeline failed — check the logs above"
        }
    }
}
