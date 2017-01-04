#!/usr/bin/env bash
remoteIP=$1
if [ -z $remoteIP ];then
	echo "need remote IP"
	exit
fi

remoteUser=$2
if [ -z $remoteUser ];then
	remoteUser=root
fi

echo "rsa.ssh $remoteUser@$remoteIP"

sshDir=`printf ~/.ssh`
#echo "sshDir = $sshDir"
if [ ! -e "$sshDir/id_rsa.pub" ];then
	if [ ! -d "$sshDir" ];then
		mkdir -p "$sshDir"
	fi
	cd "$sshDir"
	pwd
	rsaPassword=$3
	if [ -z $rsaPassword ];then
		ssh-keygen -t rsa -P ''
	else
		ssh-keygen -t rsa -P $rsaPassword
	fi
fi

sshRsa=`cat $sshDir/id_rsa.pub`
sshDir="/$remoteUser/.ssh"
echo "sshDir = $sshDir"
echo "sshRsa = $sshRsa"
foundRsa="\`cat $sshDir/authorized_keys | grep \"$sshRsa\"\`";
ssh $remoteUser@$remoteIP << remotessh

if [ ! -e "$sshDir/authorized_keys" ] || [ -z "$foundRsa" ];then
	if [ ! -d "$sshDir" ];then
		mkdir -p "$sshDir"
		chmod 600 $sshDir
	fi
	echo "$sshRsa" >> $sshDir/authorized_keys
	chmod 700 $sshDir/authorized_keys
fi

exit
remotessh