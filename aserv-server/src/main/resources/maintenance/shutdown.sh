#!/usr/bin/env bash
parentDir=`pwd`
cd `dirname $0`

url="http://127.0.0.1$1/@shutdown"
echo "curl $url"

while :
do
res=$(curl $url -s -w %{http_code})
echo "res = $res"
#$(echo $res | grep "shutDowned200")
if [[ "$res" = "000" || "$res" = "shutDowned200" ]];
then
break
fi
sleep 1
done