pipeline {
    agent any

    parameters {
        string(name: 'RELEASE_TAG', defaultValue: '', description: 'The Github release tag to deploy')
        string(name: 'NEW_VERSION', defaultValue: '', description: 'The new version to be released to the maven repository e.g. 1.0.5')
    }

    environment {
        integration_test_config = credentials('enav-services-integration-test.properties')
    }

    tools {
        maven 'M3.3.9'
    }

    triggers {
        pollSCM('* * * * *')
        cron('H * * * *')
    }

    stages {
        stage ('prepare') {
            steps {
                sh "java -version"
                echo "${currentBuild.buildCauses}"
            }
        }

        stage('build') {
//            when {
//               expression {currentBuild.buildCauses.toString().contains("SCM")}
//            }
            steps {
                withMaven(maven: 'M3.3.9', mavenOpts: '-Xmx1024m') {
//                    TODO remove -DskipTests when java problem is solved
//                  sh 'mvn -DskipTests -U clean checkstyle:check source:jar install'
                    sh 'mvn -U clean checkstyle:check source:jar install'
                }
            }
            post {
                success {
                    echo "TODO deploy to snapshot repo"
                }
            }

        }

        stage('integration test') {
            when {
                expression {currentBuild.buildCauses.toString().contains("Started by timer")}
            }
            steps {
                withMaven(maven: 'M3.3.9', mavenOpts: '-Xmx1024m ') {
                    sh "mvn -Denav-services-integration-test.configuration=file://${integration_test_config} clean test -PintegrationTest -Dmaven.node.skip=true"
                }
            }
        }

        stage('release') {
            when {
                expression { params.RELEASE_TAG != '' }
            }
            steps {
                withMaven(maven: 'M3.5.0', mavenOpts: '-Xmx1024m ') {
                    sh "mvn versions:set -DnewVersion=${params.NEW_VERSION}"
                    sh "mvn versions:update-property -DnewVersion=[${params.NEW_VERSION}] -Dproperty=enav-services.version"
                }
                withMaven(maven: 'M3.3.9', mavenOpts: '-Xmx1024m ') {
                    sh "mvn -U clean checkstyle:check source:jar install"
                }
            }
            post {
                success {
                    echo "TODO deploy to release repo"
                }
            }
        }
    }

    post {
        failure {
            // notify users when the Pipeline fails
            mail to: 'steen@lundogbendsen.dk,rmj@dma.dk',
                    subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                    body: "Something is wrong with ${env.BUILD_URL}"
        }
    }
}
