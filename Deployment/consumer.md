
sudo yum install java-1.8.0-openjdk.x86_64 -y


scp -i "rv-ec2-instance.pem" E:\Intellij\BSDS\Assignment2\Consumer\out\artifacts\Consumer_jar\Consumer.jar ec2-user@ec2-107-20-6-22.compute-1.amazonaws.com:/home/ec2-user