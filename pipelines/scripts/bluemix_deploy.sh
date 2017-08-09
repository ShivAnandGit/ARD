#! /bin/bash

source "${WORKSPACE}/pipelines/scripts/functions"

set -ex

cf logout
cf login -a $BM_API -u $BM_USER -p $BM_PASS -o $BM_ORG -s $BM_ENV
cf delete ${APP} -f -r || echo "Failed to delete old application. It may not exist. Not Fatal, Onwards!"

sed -i "s|DB_CONNECTION_STRING|$DB_CONNECTION_STRING|g" pipelines/conf/manifest.yml
sed -i "s|DB_USERNAME|$DB_USERNAME|g" pipelines/conf/manifest.yml
sed -i "s|DB_PASSWORD|$DB_PASSWORD|g" pipelines/conf/manifest.yml
sed -i "s|ENTITLEMENT_REVOKE_URL|$ENTITLEMENT_REVOKE_URL|g" pipelines/conf/manifest.yml

cf push ${APP} -f pipelines/conf/manifest.yml -p ${ZIPFILE} -t 180 -m ${MEMORY}
#Allow application to load context
sleep 60