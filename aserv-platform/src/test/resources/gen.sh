#!/usr/bin/env bash
cd `dirname $0`
currentDir=`pwd`

cmd="thrift -r --gen $1 -o gen thrift/tplatform.thrift"
echo $cmd
`$cmd`