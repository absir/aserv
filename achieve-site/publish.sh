#!/usr/bin/env bash
cd `dirname $0`

mvn archetype:create-from-project

cd target/generated-sources/archetype

mvn install