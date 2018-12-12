pipeline {
    agent any

    stages {
        stage('build') {
            steps {
                withMaven() {
                    sh '-U clean checkstyle:check source:jar install'
                }
            }
        }

    }

}

