#!/usr/bin/env bash
parentDir=`pwd`
cd `dirname $0`
currentDir=`pwd`

cd '../daemon/'
echo > shutdown
sleep 5

while [ -e stopping ] && [ ! -e stopped ]
do
	sleep 1
done

cd "$parentDir"