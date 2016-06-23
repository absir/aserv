#!/usr/bin/env bash
cd `dirname $0`

./clear.sh

rm -rf target/generated-sources/archetype

mvn archetype:create-from-project

cd target/generated-sources/archetype

rm -rf src/main/resources/archetype-resources/*.iml
rm -rf src/main/resources/archetype-resources/*.sh
rm -rf src/main/resources/archetype-resources/read.ME
rm -rf src/main/resources/archetype-resources/.idea
rm -rf src/main/resources/archetype-resources/*.ico
rm -rf src/main/resources/archetype-resources/*.png

rm -rf src/main/resources/archetype-resources/src/main/webapp/static
rm -rf src/main/resources/archetype-resources/src/main/webapp/lib

rm -rf src/main/resources/archetype-resources/src/main/webapp/admin
rm -rf src/main/resources/archetype-resources/src/main/webapp/WEB-INF/developer
rm -rf src/main/resources/archetype-resources/src/main/webapp/WEB-INF/tpl

mvn install