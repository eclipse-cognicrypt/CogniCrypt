SSHPASS=$PASSWORD sshpass -e sftp $USERNAME@build.eclipse.org
cd /home/data/httpd://download.eclipse.org/cognicrypt
lcd /target/repository
put -r *
bye