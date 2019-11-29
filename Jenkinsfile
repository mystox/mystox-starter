pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
            args '-v /home/mystox/respository:/root/.m2'
        }
    }
    stages {
        stage ('Build') {
            steps {
                sh 'mvn -B -DskipTests clean package --settings /root/.m2/settings.xml'
            }
        }
    }
}