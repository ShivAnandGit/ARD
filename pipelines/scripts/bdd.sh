#! /bin/bash

WORKSPACE=${WORKSPACE:-`pwd`}

source ${WORKSPACE}/pipelines/scripts/functions

if [ -z "${invokeBDD}" ]
    then
      echo "FAILED: invokeBDD Unset. Dont know how to test."
      exit 1;
fi
find tests/acceptance/wdio/ -type f | xargs sed -i 's#${API_ENDPOINT}#'${APP}'#g'
set -ex
hostname 
pwd
touch -c -t 12101630  * || true
rm -rf node_modules
rm -rf zap-report ; mkdir -p zap-report
docker-compose build node-bdd-embedded-zap
docker-compose run -e no_proxy=localhost,127.0.0.1,sandbox.local,lbg.eu-gb.mybluemix.net,lbg.eu-gb.bluemix.net,10.113.140.170,10.113.140.179,10.113.140.187,10.113.140.168,jenkins.sandbox.extranet.group,nexus.sandbox.extranet.group,gerrit.sandbox.extranet.group,sonar.sandbox.extranet.group,gatewaydata01.psd2.sandbox.extranet.group --rm node-bdd-embedded-zap bash -c "ln -sf ../node_modules; ${invokeBDD} "
zapContainer=`docker-compose ps |grep zap |  head -1 | awk {'print $1'}`
sudo docker exec $zapContainer zap-cli report -f html -o report.html
sudo docker exec $zapContainer zap-cli report -f xml -o report.xml
sudo docker exec $zapContainer cat report.html >zap-report/report.html
sudo docker exec $zapContainer cat report.xml >zap-report/report.xml
docker-compose kill
docker-compose rm -f

# Remove spaces from outputfiles
cd ${RESULTSDIR}
find . -type f -name "* *.json" -exec bash -c 'mv "$0" "${0// /_}"' {} \;  || true

# Remove absolute paths from BDD json. Dont let this fail the build
sed -i 's+'${WORKSPACE}'/++g'  *.* || true
