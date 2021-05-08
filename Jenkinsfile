pipeline {
    agent any

    tools {
        maven 'apache-maven-latest'
        jdk 'oracle-jdk8-latest'
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Publish Snapshot'){
            when { 
                branch 'develop';
            }
            steps {
                sshagent ( ['projects-storage.eclipse.org-bot-ssh']) {
                    sh '''
                    ssh genie.cognicrypt@projects-storage.eclipse.org rm -rf /home/data/httpd/download.eclipse.org/cognicrypt/snapshot
                    ssh genie.cognicrypt@projects-storage.eclipse.org mkdir -p /home/data/httpd/download.eclipse.org/cognicrypt/snapshot
                    scp -r repository/target/repository/* genie.cognicrypt@projects-storage.eclipse.org:/home/data/httpd/download.eclipse.org/cognicrypt/snapshot
                    '''
                }
            }
        }  
        stage('Publish Stable'){
            when { 
                branch 'master';
            }
            steps {
                sshagent ( ['projects-storage.eclipse.org-bot-ssh']) {
                    sh '''
                    ssh genie.cognicrypt@projects-storage.eclipse.org rm -rf /home/data/httpd/download.eclipse.org/cognicrypt/stable
                    ssh genie.cognicrypt@projects-storage.eclipse.org mkdir -p /home/data/httpd/download.eclipse.org/cognicrypt/stable
                    scp -r repository/target/repository/* genie.cognicrypt@projects-storage.eclipse.org:/home/data/httpd/download.eclipse.org/cognicrypt/stable
                    '''
                }
            }
        }  
    }
}
