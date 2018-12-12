pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * *')

    }
    stages {
        stage('build') {
            steps {
                withMaven() {
                    sh '-U clean checkstyle:check source:jar install'
                }
            }
        }

    }

    post {
        failure {
            // notify users when the Pipeline fails
            mail to: 'steen@lundogbendsen.dk',
                    subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                    body: "Something is wrong with ${env.BUILD_URL}"
        }
    }

}

