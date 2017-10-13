#! /bin/bash

source "${WORKSPACE}/pipelines/scripts/functions"

set -ex

cf logout
cf login -a $BM_API -u $BM_USER -p $BM_PASS -o $BM_ORG -s $BM_ENV
cf delete ${APP} -f -r || echo "Failed to delete old application. It may not exist. Not Fatal, Onwards!"

cf push ${APP} -f pipelines/conf/manifest.yml -p ${ZIPFILE} -t 180 -m ${MEMORY}
#Allow application to load context
sleep 60
