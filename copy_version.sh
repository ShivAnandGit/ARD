#!/bin/sh
#Created by sthak4
# shell script to read and act on microservice metadata file

# source the properties:
. ./microservice.metadata

# Then reference them:
echo "version is $version and context_root is $context_root"

#Set to to the pom
mvn versions:set -DnewVersion=$version

#Version manipulation
inputVersion=$version
inputVersionArr=(${inputVersion//./ })

version=${inputVersionArr[0]}.${inputVersionArr[1]}

version=v$version
echo "Modified version is $version"
#sed -i "s|context_root=[^ ]*/context_root=$context_root/g" ./src/main/wlp/bootstrap.properties

#mvn clean install

setProperty(){
  awk -v pat="^$1$4" -v value="$1$4 $2" '{ if ($0 ~ pat) print value; else print $0; }' $3 > $3.tmp
  mv $3.tmp $3
}

setjsonProperty(){
value=$context_root/$version
echo $value
sed -i -r 's|contextPath:.*|contextPath: '"$value"'|' $1
}

############################
### usage: setProperty $key $value $filename $separator
setProperty "context-root"              $context_root/$version     ./src/main/wlp/bootstrap.properties			=
setProperty "  version"               	 $inputVersion     			    ./src/main/resources/ob-cnf-account-request-data-api-swagger.yaml	:
setProperty "basePath"               	 $context_root/$version     ./src/main/resources/ob-cnf-account-request-data-api-swagger.yaml	:
setjsonProperty ./src/main/resources/application.yml
setjsonProperty ./src/main/wlp/config/application.yml
