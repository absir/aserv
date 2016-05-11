#!/usr/bin/env bash
project=$1
if [ -z $project ];then
printf "至少需要工程名称"
exit
fi

ext="-DgroupId="

group=$2
if [ -z $group ];then
ext=$ext"com.absir"
else
ext=$ext$group
fi

command="mvn archetype:generate -DarchetypeCatalog=local -DinteractiveMode=false -DarchetypeGroupId=com.absir.archetype -DarchetypeArtifactId=achieve-site-archetype -DarchetypeVersion=1.0-Final -DartifactId=$project $ext"
printf "$command"
$command