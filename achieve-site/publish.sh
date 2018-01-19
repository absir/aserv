#!/usr/bin/env bash
cd `dirname $0`

./clear.sh

rm -rf target/generated-sources/archetype

mvn archetype:create-from-project

cd target/generated-sources/archetype

archetypeRM(){
    command="rm -rf src/main/resources/archetype-resources/$1"
    $command
    command="rm -rf target/classes/archetype-resources/$1"
    $command
}

archetypeRM *.iml
archetypeRM *.sh
archetypeRM .idea
#archetypeRM .ico
#archetypeRM .png

archetypeRM src/main/webapp/static
archetypeRM src/main/webapp/lib

archetypeRM src/main/webapp/admin
archetypeRM src/main/webapp/WEB-INF/developer
archetypeRM src/main/webapp/WEB-INF/tpl

archetypeRM jetty/webapps
archetypeRM jetty/work
archetypeRM jetty/*.out
archetypeRM jetty/*.log

archetypeRM webResources

rm -rf **/.idea

mvn install

#./clear.sh