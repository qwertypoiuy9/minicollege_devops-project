pipeline {
    agent any
    
    tools {
        maven 'Maven3'
        jdk 'Java21'
    }
    
    environment {
        // CRITICAL: Forces Maven to use the Java 21 tool we configured in Jenkins
        JAVA_HOME = "${tool 'Java21'}"
        DOCKER_IMAGE = 'minicollege-app'
        DOCKER_TAG = '1.0.0'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                // Verify Java version to ensure the fix worked
                sh 'java -version'
                sh 'mvn clean compile'
            }
        }
        
        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        
        /* 
        // TODO: Uncomment in Week 3 after configuring SonarQube in Jenkins
        stage('Code Analysis - SonarQube') {
            steps {
                script {
                    withSonarQubeEnv('SonarQube') {
                        sh '''
                            mvn sonar:sonar \
                                -Dsonar.projectKey=minicollege \
                                -Dsonar.projectName='MiniCollege' \
                                -Dsonar.host.url=http://YOUR_EC2_IP:9000 \
                                -Dsonar.login=${SONAR_TOKEN}
                        '''
                    }
                }
            }
        }
        */
        
        stage('Security Scan - Gitleaks') {
            steps {
                // || true allows the pipeline to continue for now, but we will remove it later 
                // so that finding a secret actually FAILS the build and triggers the AI.
                sh '''
                    docker run --rm -v $WORKSPACE:/src zricethezav/gitleaks:latest detect -v -s /src || true
                '''
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
            }
        }
        
        stage('Vulnerability Scan - Trivy') {
            steps {
                sh '''
                    docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
                        aquasec/trivy:latest image ${DOCKER_IMAGE}:${DOCKER_TAG} || true
                '''
            }
        }
        
        /* 
        // TODO: Uncomment in Week 5 for Kubernetes deployment (Stretch Goal)
        // For now, we will use Docker Compose for deployment as per AEGIS recommendations.
        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                    kubectl apply -f k8s/deployment.yaml || true
                    kubectl rollout restart deployment/minicollege || true
                '''
            }
        }
        */
    }
    
    post {
        always {
            echo 'Pipeline completed'
            cleanWs()
        }
        failure {
            echo 'Pipeline failed - AI Agent will be triggered'
            // Week 4: We will add the curl command here to trigger your Python FastAPI agent
            // sh 'curl -X POST http://YOUR_EC2_IP:8000/remediate -H "Content-Type: application/json" -d "{\"build_id\": \"${BUILD_NUMBER}\", \"job_name\": \"${JOB_NAME}\"}"'
        }
    }
}