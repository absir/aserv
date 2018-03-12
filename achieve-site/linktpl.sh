#!/usr/bin/env bash

toDir=`pwd`

if [ ! -z $1 ];then
    toDir="$toDir/$1"
fi

cd `dirname $0`
currentDir=`pwd`

echo "$currentDir=>$toDir"

fromPath="$currentDir/src/main/webapp"
toPath="$toDir/src/main/webapp"

if [ -d "$toPath/static" ]; then
	echo "$toPath/static dir exist"
	exit 0
fi

ln -s "$fromPath/static" "$toPath/static"
ln -s "$fromPath/lib" "$toPath/lib"
ln -s "$fromPath/WEB-INF/developer" "$toPath/WEB-INF/developer"
ln -s "$fromPath/WEB-INF/tpl" "$toPath/WEB-INF/tpl"

mkdir "$toPath/../webapp0"