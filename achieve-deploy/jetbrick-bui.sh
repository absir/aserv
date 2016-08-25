#!/usr/bin/env bash

cd `dirname $0`

webPath="../achieve-site/src/main/webapp"
deployName="jetbrick-bui"

deployPath="./deploy/$deployName"

rm -rf "$deployPath"

#if [ ! -d "$deployPath" ]; then
  mkdir -p "$deployPath"
  mkdir "$deployPath/WEB-INF"
#fi

cp -r "$webPath/lib" "$deployPath/"
cp -r "$webPath/WEB-INF/developer" "$deployPath/WEB-INF/"
cp -r "$webPath/WEB-INF/tpl" "$deployPath/WEB-INF/"

./build.sh $deployName

rm -rf "$deployPath"