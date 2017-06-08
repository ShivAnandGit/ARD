/*
 * Author: Abhay Chrungoo <abhay@ziraffe.io>
 * Contributing HOWTO: TODO
 */

def pack(String targetBranch, String targetEnv, context) {
	node() {
		checkout scm
		this.packHandler(targetBranch, targetEnv, context)
	}
}

def packHandler(String targetBranch, String targetEnv, context) {

	String  mavenSettings  = context.config.maven.settings ?: 'pipelines/conf/settings.xml'
	String  mavenPom = context.config.maven.pom ?: 'pom.xml'
	String  mavenGoals = context.config.mavengoals.package?: "-U clean package -DskipTests=true"
	try {
		dir('target'){deleteDir()}
		sh """source pipelines/scripts/functions && \\
								mvn ${mavenGoals} 	\\
									-s ${mavenSettings} \\
					 				-f ${mavenPom} \\
									"""
		dir('target'){
			archiveArtifacts '*.war,*.jar'
			stash name: "artifact-${context.application}-${targetBranch}", includes: '*.war,*.jar'
		}
	} catch (error) {
		echo "Caught: ${error}"
		echo "Application Build failed"
		throw error
	} finally {
		step([$class: 'WsCleanup', notFailBuild: true])
	}
}

def publishNexus(String targetBranch, String targetEnv, context){
	node() {
		checkout scm
		this.publishNexusHandler(targetBranch, targetEnv, context)
	}
}
def publishNexusHandler(String targetBranch, String targetEnv, context){
	def artifactName
	String  nexusURL = context.config.nexus.url ?: 'http://invalid.url/'
	def targetCommit =  sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
	def pomVersion = sh(returnStdout: true,	script: 'pipelines/scripts/maven_version.sh').trim()

	def version = "${pomVersion}+${targetBranch}.${targetCommit}.${env.BUILD_NUMBER}"

	try {
		dir ('j2'){
			deleteDir()
			unstash "artifact-${context.application}-${targetBranch}"
			artifactName =  sh(returnStdout: true, script: 'ls *.?ar | head -1').trim()
		}
		withCredentials([
			usernamePassword(credentialsId: 'nexus-snapshots',
			passwordVariable: 'NEXUS_PASS',
			usernameVariable: 'NEXUS_USER')
		]) {
			echo "PUBLISH: ${this.name()} artifact ${artifactName} v:${version} to ${nexusURL} "
			withEnv([
				"version=${version}",
				"artifact=${artifactName}",
				"url=${nexusURL}"
			]){ sh 'pipelines/scripts/maven_deploy.sh'}
		}
	} catch (error) {
		echo "Failed to publish artifact to Nexus"
	} finally {	}
}

def name() {
	return "Maven"
}


return this;
