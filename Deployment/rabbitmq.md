https://dev.to/ashutosh049/install-rabbitmq-on-amamzon-ec2-amazon-linux-2-3dpd

https://www.rabbitmq.com/install-rpm.html

#Connect EC2
```shell script
ssh -i "rv-ec2-instance.pem" ec2-user@ec2-3-87-62-207.compute-1.amazonaws.com
```
# Install

### Update all the packages to the latest version available.
```shell script
sudo yum -y update
```
### Package Dependencies before installing RabbitMq
In order to install rabbitmq, we need to have these packages available:
- erlang
- socat
- logrotate

### Enable the EPEL repository
Standard repositories might not provide all the packages that can be installed on CentOS, Red Hat Enterprise Linux (RHEL), or Amazon Linux-based distributions. Enabling the EPEL repository provides additional options for package installation

1. Amazon Linux 2

   Install the EPEL release package for EL 7 and enable the EPEL repository.
   ```shell script
   sudo yum install -y https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm
   ```
   ```shell script
   sudo yum-config-manager --enable epel
   ```
2. Amazon Linux AMI

The EPEL repository is already installed on the original version of Amazon Linux, but you must enable it. You can enable this repository either by using the yum-config-manager command or by editing the epel.repo file.
sudo yum-config-manager --enable epel

See more at [ec2-enable-epel](https://aws.amazon.com/premiumsupport/knowledge-center/ec2-enable-epel/)

### Install Erlang
```shell script
sudo yum install erlang --enablerepo=epel
```

>TIP: Prevent Erlang unwanted upgrades
>To do this, follow:
>Install package named yum-plugin-versionlock (called yum-versionlock in RHEL 5).
>``` shell script
>yum install yum-plugin-versionlock
>```
>The /etc/yum/pluginconf.d/versionlock.list will be created on the system.
>To install or lock the version of the gcc package, add that package name to the /etc/yum/pluginconf.d/versionlock.list file by running:
>``` shell script
>yum versionlock gcc-*
>```
Alternatively, you can edit the filelist, ```/etc/yum/pluginconf.d/versionlock.list```, directly.

The above configuration will not allow to upgrade the gcc package to version greater than what was installed at the time the locking was performed.
Yum will attempt to update all packages, while excluding the packages listed in the versionlock file.


###Install socat
```shell script
sudo yum install -y socat
```
###Install logrotate
```shell script
sudo yum install logrotate
```
###Install RabbitMq
```shell script
## install RabbitMQ and zero dependency Erlang from the above repositories,
## ignoring any versions provided by the standard repositories
sudo yum install --repo rabbitmq_erlang --repo rabbitmq_server erlang rabbitmq-server -y
```

# START/STOP
START:
```shell script
sudo service rabbitmq-server start
```
STOP:
```shell script
sudo service rabbitmq-server stop
```
STATUS:
```shell script
sudo service rabbitmq-server status
```

#Create new user
Add a new/fresh user, say user test and password test:
```shell script
sudo rabbitmqctl add_user bsds_server bsds_server
```
Give administrative access to the new user:
```shell script
sudo rabbitmqctl set_user_tags bsds_server administrator
```
Set permission to newly created user:
```shell script
sudo rabbitmqctl set_permissions -p / bsds_server ".*" ".*" ".*"
```
Now try to login via guest1/guest1