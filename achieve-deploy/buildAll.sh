#!/usr/bin/env bash

cd `dirname $0`
currentDir=`pwd`

for file in ./deploy/*
do
    if [ -d $file ];then
        cd "$currentDir"
        ./build.sh "$file"
    fi
done

cd "$currentDir"