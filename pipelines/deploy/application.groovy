/*
 * Author: Abhay Chrungoo <abhay@ziraffe.io>
 */

def deploy(String targetBranch, context) {
	node() {
		unstash "pipelines-${context.application}-${targetBranch}"
		this.deployHandler(targetBranch, context)
	}
}

def deployHandler(String targetBranch, context) {
	def appName = appName(context, targetBranch)
	def artifactName
	String  memoryAllocation = context.config.environments.master.memory ?: '512M'
	String  bluemixDomain =  context.config.bluemix.domain ?: 'lbg.eu-gb.bluemix.net'
	String  bluemixAPI = context.config.bluemix.api ?: 'api.lbg.eu-gb.bluemix.net'
	String  bluemixEnv = context.config.environments.master.bluemix.env ?: 'INVALID'
	String  bluemixOrg = context.config.environments.master.bluemix.org ?: 'INVALID'
	String  bluemixCredentials = context.config.bluemix.credentials ?: 'bluemix-global-deployer'
	dir('artifacts'){
		unstash "artifact-${context.application}-${targetBranch}"
		artifactName = sh(script: "ls *.?ar| head -1", returnStdout: true).trim()
	}
	withCredentials([
		usernamePassword(credentialsId: bluemixCredentials,
		passwordVariable: 'BM_PASS',
		usernameVariable: 'BM_USER')
	]) {
		withEnv([
			"BM_API=${bluemixAPI}",
			"BM_DOMAIN=${bluemixDomain}",
			"BM_ORG=${bluemixOrg}",
			"BM_ENV=${bluemixEnv}",
			"MEMORY=${memoryAllocation}",
			"APP=${appName}",
			"CF_HOME=${env.WORKSPACE}",
			"WARFILE=artifacts/${artifactName}"
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
	String  bluemixDomain =  context.config.bluemix.domain ?: 'lbg.eu-gb.bluemix.net'
	String  bluemixAPI = context.config.bluemix.api ?: 'api.lbg.eu-gb.bluemix.net'
	String  bluemixEnv = context.config.environments.master.bluemix.env ?: 'INVALID'
	String  bluemixOrg = context.config.environments.master.bluemix.org ?: 'INVALID'
	String  bluemixCredentials = context.config.bluemix.credentials ?: 'bluemix-global-deployer'

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
