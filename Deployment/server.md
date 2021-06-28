# install tomcat
sudo -s
yum install java-1.8.0-openjdk.x86_64 -y
cd /opt
wget https://archive.apache.org/dist/tomcat/tomcat-8/v8.5.35/bin/apache-tomcat-8.5.35.tar.gz
tar -xzvf /opt/apache-tomcat-8.5.35.tar.gz
cd apache-tomcat-8.5.35/bin
chmod +x startup.sh shutdown.sh
./startup.sh
cd
cd /opt/apache-tomcat-8.5.35/
sudo chmod -R 777 webapps/


# tomcat webapp path

## A1
```shell script
scp -i "rv-ec2-instance.pem" E:\Intellij\BSDS\Assignment1\Server\out\artifacts\Server_war\Server_war.war  ec2-user@ec2-3-88-13-78.compute-1.amazonaws.com:/opt/apache-tomcat-8.5.35/webapps/
```
## A2
```shell script
scp -i "rv-ec2-instance.pem" E:\Intellij\BSDS\Assignment2\Server\out\artifacts\Server_war\Server_war.war ec2-user@ec2-3-84-206-191.compute-1.amazonaws.com:/opt/apache-tomcat-8.5.35/webapps/
```

## A3
```shell script
scp -i "rv-ec2-instance.pem" E:\Intellij\BSDS\Assignment3\Server\out\artifacts\Server_war\Server_war.war  ec2-user@ec2-50-19-42-157.compute-1.amazonaws.com:/opt/apache-tomcat-8.5.35/webapps/
```