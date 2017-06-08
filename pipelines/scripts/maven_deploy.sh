#!/bin/bash

source "${WORKSPACE}/pipelines/scripts/functions"   &>/dev/null

set -ex

mvn deploy:deploy-file \
	-DpomFile=pom.xml \
	-s pipelines/conf/settings.xml \
	-Dversion="${version}"  \
	-Durl="${url}" \
	-DrepositoryId=deploy-repo \
	-Dnexus.user="${NEXUS_USER}" \
	-Dnexus.password="${NEXUS_PASS}" \
	-Dfile="j2/${artifact}" 