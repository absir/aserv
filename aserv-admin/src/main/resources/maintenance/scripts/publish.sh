#!/usr/bin/env bash
cd `dirname $0`

sshRemote=$1
if [ -z $sshRemote ];then
	echo "need sshRemote [remoteUser@]remoteIP"
	exit
fi

if [[ `echo $sshRemote | grep "@"` = "" ]];then
	$sshRemote="root@$sshRemote"
fi

targetDeploy=$2
if [ -z $targetDeploy ] || [ ! -x $targetDeploy ];then
	echo "could not found targetDeploy = $targetDeploy"
	exit
fi

configSource=$3
if [ -z $configSource ];then
	configSource="config.sh"
fi

echo "public $sshRemote $targetDeploy $configSource"

cd "../config"
source "$configSource"

myConf=`cat $_MY_CONF`
initDb=`cat $_INIT_DB`

ssh $remoteUser@$remoteIP << remotessh

if [ -x "~/_ab_maintenance_/$INIT_LOCK" ]

	$APT_UPDATE

	$APT_INSTALL mysql-server tomcat7 tomcat7-common

	echo "$myConf" > $MY_CONF

	$INIT_MYSQL restart

	mysql -hlocalhost -uroot -p << mysqlCli

	$initDb

	exit;

	mysqlCli

	echo > "~/_ab_maintenance_/$INIT_LOCK"

fi

exit
remotessh