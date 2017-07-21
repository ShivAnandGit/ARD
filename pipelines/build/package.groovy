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

	def mavenSettings  = context.config.maven.settings ?: 'pipelines/conf/settings.xml'
	def mavenPom = context.config.maven.pom ?: 'pom.xml'
	def mavenGoals = context.config.mavengoals.package?: "-U clean package"
	try {
		dir('target'){deleteDir()}
		sh """source pipelines/scripts/functions && \\
								mvn ${mavenGoals} 	\\
									-s ${mavenSettings} \\
					 				-f ${mavenPom} \\
									"""
		dir('target'){
			archiveArtifacts '*.zip'
			stash name: "artifact-${context.application}-${targetBranch}", includes: '*.zip'
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
	String artifactName
	String nexusURL = context.config.nexus.url ?: 'http://invalid.url/'
	String targetCommit =  sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
	String classifier='SNAPSHOT'
	String branchIdentifier = targetBranch.take(2)
	switch(targetEnv) {
		case 'integration':
			if (targetBranch.startsWith('release')){
				classifier = "release.${env.BUILD_NUMBER}"
			} else if(targetBranch == 'master') {
				classifier = "rc.${env.BUILD_NUMBER}"
			} else if(targetBranch.startsWith('hotfix')) {
				classifier = "hotfix.${env.BUILD_NUMBER}"
			} else{
				classifier = "${branchIdentifier}.${env.BUILD_NUMBER}"
			}
			break;
		case 'feature': classifier = "SNAPSHOT" ; break ;
		default: classifier = "SNAPSHOT" ; break ;
	}

	try {
		dir ('j2'){
			deleteDir()
			unstash "artifact-${context.application}-${targetBranch}"
			artifactName =  sh(returnStdout: true, script: 'ls *.zip | head -1').trim()
		}
		withCredentials([
			usernamePassword(credentialsId: 'nexus-uploader',
			passwordVariable: 'NEXUS_PASS',
			usernameVariable: 'NEXUS_USER')
		]) {
			echo "PUBLISH: ${this.name()} artifact ${artifactName} to ${nexusURL} "
			withEnv([
				"classifier=${classifier}",
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
