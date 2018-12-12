pipeline {
    agent any

    tools {
        maven 'M3.3.9'
    }

    triggers {
        pollSCM('H/5 * * * *')

    }
    stages {
        stage('build') {
            steps {
                withMaven(maven: 'M3.3.9', jdk: 'java_openjdk_8', mavenOpts: '-Xmx1024m  -XX:MaxPermSize=512m') {
                    sh 'mvn -U clean checkstyle:check source:jar install'
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

