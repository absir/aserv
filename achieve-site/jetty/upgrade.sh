#!/usr/bin/env bash
cd `dirname $0`
currentDir=`pwd`

case $1 in
	_nohup_ )
./jetty.sh restart $2
#wait nohup startup
sleep 30
;;

_jar_ )
nohup sh ./upgrade.sh _nohup_ $2 > restart.log &
sleep 30
;;

* )
java -jar ab-exec.jar ./upgrade.sh _jar_ $1
esac


