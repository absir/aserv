#!/usr/bin/env bash
cd `dirname $0`
currentDir=`pwd`

name=$1
if [ -z $name ];then
printf "need project name"
exit
fi

name="${name##*/}"

file="$currentDir/deploy/$name"
if [ -d $file ];then
    cd "$file"
    rm -rf "$currentDir/$name.zip"
#    if [ -f "$file/build.sh" ];then
#        "$file/build.sh"
#    fi
    zip -r "$currentDir/$name.zip" "./"
else
    echo "project not exist $file"
fi

cd "$currentDir"