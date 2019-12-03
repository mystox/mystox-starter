pipeline {
    parameters {
        string(host60:'172.16.5.60', host65:'172.16.5.65')
    }
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
    post {
        success {
            script {
                stage ('pushJar') {
                  def remote = [:]
                  remote.name = 'controller'
                  remote.user = 'root'
                  remote.password = 'yytd1234'
                  remote.allowAnyHosts = true
                  writeFile file: 'abc.sh', text: 'ls'
                  sshCommand remote: remote, command: 'for i in {1..5}; do echo -n \"Loop \$i \"; date ; sleep 1; done', override: true
                  sshPut remote: remote, from: 'abc.sh', into: '.'
                }
            }
        }
    }
}