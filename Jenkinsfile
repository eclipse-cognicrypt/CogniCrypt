pipeline {
    agent any

    tools {
         jdk 'Oracle JDK 8'
    }

    stages {

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }


		stage('Deploy'){
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
            when { 
                branch 'develop';
            }
            steps {
                sshagent ( ['projects-storage.eclipse.org-bot-ssh']) {
                    sh '''
                    ssh genie.cognicrypt@projects-storage.eclipse.org rm -rf /home/data/httpd/download.eclipse.org/cognicrypt/snapshots
                    ssh genie.cognicrypt@projects-storage.eclipse.org mkdir -p /home/data/httpd/download.eclipse.org/cognicrypt/snapshots
                    scp -r repository/target/repository/* genie.cognicrypt@projects-storage.eclipse.org:/home/data/httpd/download.eclipse.org/cognicrypt/snapshots
                    '''
                }
            }
		}
    }
}