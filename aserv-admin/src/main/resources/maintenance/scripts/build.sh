#!/usr/bin/env bash
cd `dirname $0`
currentDir=`pwd`

cd "../../../../../"

project=$1
if [ ! -z $project ];then
	echo "build project = $project"
	cd "../$project"
fi

echo `pwd`

mvn clean

sed -i -e 's/version=\([^.]*\).*/version=\1.'$(date +%g.%m%d.%H%M)'/g' src/main/resources/config.properties

rm -rf src/main/resources/config.properties-e

mvn package
