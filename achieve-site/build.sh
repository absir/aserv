#!/usr/bin/env bash
cd `dirname $0`
currentDir=`pwd`

maven=1
jetty=0
case $1 in
	all)
jetty=1
;;
jetty)
maven=0
jetty=1
;;
*)
esac

if [ $maven -gt 0 ];then

	#更新版本信息
	sed -i -e 's/version=\([^.]*\).*/version=\1.'$(date +%g.%m%d.%H%M)'/g' src/main/resources/config.properties
	rm -rf src/main/resources/config.properties-e

	mvn clean
	mvn package

	if [ ! -d "jetty/work" ];then
		mkdir jetty/webapps
		mkdir jetty/work
		ln -s $currentDir/webResources jetty/work/webResources
	fi

	cp -rf target/achieve-site-1.0-Final.war jetty/webapps/ROOT.war
fi

if [ $jetty -gt 0 ];then
    rm -rf jettyDeploy.gz
	tar -czvf jettyDeploy.gz --exclude=jetty/*.out --exclude=jetty/.* --exclude=jetty/work jetty
fi

