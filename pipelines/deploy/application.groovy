/*
 * Author: Abhay Chrungoo <abhay@ziraffe.io>
 */

def deploy(String targetBranch, context) {
	node() {
	//	unstash "pipelines-${context.application}-${targetBranch}"
		checkout scm
		this.deployHandler(targetBranch, context)
	}
}

def deployHandler(String targetBranch, context) {
	def appName = appName(context, targetBranch)
	def artifactName
	def dbConnectionString
	def dbUsername
	def dbPassword
	def entitlementRevokeUrl
	def memoryAllocation = context.config.environments.master.memory ?: '512M'
	def bluemixDomain =  context.config.bluemix.domain ?: 'lbg.eu-gb.bluemix.net'
	def bluemixAPI = context.config.bluemix.api ?: 'api.lbg.eu-gb.bluemix.net'
	def bluemixEnv = context.config.environments.master.bluemix.env ?: 'INVALID'
	def bluemixOrg = context.config.environments.master.bluemix.org ?: 'INVALID'
	def bluemixCredentials = context.config.bluemix.credentials ?: 'bluemix-global-deployer'
	dir('artifacts'){
		unstash "artifact-${context.application}-${targetBranch}"
		artifactName = sh(script: "ls *.zip| grep -v config.zip | head -1", returnStdout: true).trim()
	}
	switch (targetBranch) {
		case 'master':
			dbUsername = context.config.environments.master.dbUsername
			dbPassword = context.config.environments.master.dbPassword
			dbConnectionString = context.config.environments.master.dbConnectionString
			entitlementRevokeUrl = context.config.environments.master.entitlementRevokeUrl
			break
		default:
			dbUsername = context.config.environments.ci.dbUsername
			dbPassword = context.config.environments.ci.dbPassword
			dbConnectionString = context.config.environments.ci.dbConnectionString
			entitlementRevokeUrl = context.config.environments.ci.entitlementRevokeUrl
			break
		}
	withCredentials([
		usernamePassword(credentialsId: bluemixCredentials,
		passwordVariable: 'BM_PASS',
		usernameVariable: 'BM_USER')
	]) {
		withEnv([
			"DB_USERNAME=${dbUsername}",
			"DB_PASSWORD=${dbPassword}",
			"DB_CONNECTION_STRING=${dbConnectionString}",
			"ENTITLEMENT_REVOKE_URL=${entitlementRevokeUrl}",
			"BM_API=${bluemixAPI}",
			"BM_DOMAIN=${bluemixDomain}",
			"BM_ORG=${bluemixOrg}",
			"BM_ENV=${bluemixEnv}",
			"MEMORY=${memoryAllocation}",
			"APP=${appName}",
			"CF_HOME=${env.WORKSPACE}",
			"ZIPFILE=artifacts/${artifactName}"
		]) {
			try {
				sh 'pipelines/scripts/bluemix_deploy.sh'
			} catch (error) {
				echo "Deployment failed"
				throw error
			} finally {
				step([$class: 'WsCleanup', notFailBuild: true])
			}
		}
	}
}

def purge(String targetBranch, context) {
	if(targetBranch.startsWith('master')){
		echo "SKIPPING: Wont terminate ${targetBranch} bluemix environment"
	}else{
		node() {
			unstash "pipelines-${context.application}-${targetBranch}"
			this.purgeHandler(targetBranch, context)
		}
	}
}

def purgeHandler(String targetBranch, context) {
	def appName = appName(context, targetBranch)
	def bluemixDomain =  context.config.bluemix.domain ?: 'lbg.eu-gb.bluemix.net'
	def bluemixAPI = context.config.bluemix.api ?: 'api.lbg.eu-gb.bluemix.net'
	def bluemixEnv = context.config.environments.master.bluemix.env ?: 'INVALID'
	def bluemixOrg = context.config.environments.master.bluemix.org ?: 'INVALID'
	def bluemixCredentials = context.config.bluemix.credentials ?: 'bluemix-global-deployer'

	withCredentials([
		usernamePassword(credentialsId: 'bluemix-global-deployer',
		passwordVariable: 'BM_PASS',
		usernameVariable: 'BM_USER')
	]) {
		withEnv([
			"BM_API=${bluemixAPI}",
			"BM_DOMAIN=${bluemixDomain}",
			"BM_ORG=${bluemixOrg}",
			"BM_ENV=${bluemixEnv}",
			"APP=${appName}"
		]) {
			try {
				sh 'pipelines/scripts/bluemix_destroy.sh'
			} catch (error) {
				echo "Cleanup  failed. Not fatal, Onwards!!"
			} finally {
				step([$class: 'WsCleanup', notFailBuild: true])
			}
		}
	}
}

def name() {
	return "bluemix"
}

return this;

