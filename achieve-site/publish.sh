#!/usr/bin/env bash
cd `dirname $0`

rm -rf target/generated-sources/archetype

mvn archetype:create-from-project

cd target/generated-sources/archetype

rm -rf src/main/resources/archetype-resources/*.iml
rm -rf src/main/resources/archetype-resources/*.sh
rm -rf src/main/resources/archetype-resources/read.ME

rm -rf target/classes/archetype-resources/*.iml
rm -rf target/classes/archetype-resources/*.sh
rm -rf target/classes/archetype-resources/read.ME

mvn install