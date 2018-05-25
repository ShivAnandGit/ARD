def runTest(String targetBranch, context) {
    def gitBddRepo = context.config.bdd.git_repo
//    String label = context.config.builder.label
    String  gitBddCreds = context.config.bdd.git_credentials?: 'ob-codeuser'
    def gitBddBranch = this.findCorrespondingBDDBranch()


    node() {
        try{
            git branch: gitBddBranch, url: gitBddRepo, credentialsId: gitBddCreds
        }catch(error){
            echo "FAILED: Cannot find a matching  bdd branch to test against"
            throw error
        }
        unstash "pipelines-${context.application}-${targetBranch}"
        this.runTestHandler(targetBranch, context)
    }
}
def runTestHandler(String targetBranch, context) {
    def app = appName(context,targetBranch)
    def zapQualityGate = context.config.zap.sonarQuality_gate ?: 'invalid-gate'
    def zapResultDir = context.config.zap.resultdir ?: "invalid-path"
    def sonarServerID = context.config.sonar.server_id ?: 'invalid-sonarServer'
    String  invokeBDD = context.config.bdd.invocation ?: 'fail'
    String  scenarioPassThreshold = context.config.bdd.passthresholds?: '90'

def sonarJavaOptions = [
       '-Dsonar.projectKey': app + '-zap' ,
       '-Dsonar.projectName': app  + '-zap',
       '-Dappname': app  + '-zap',
       '-DbranchName': targetBranch ,
       '-Dsonar.projectVersion': env.BUILD_NUMBER ,
       '-Dsonar.projectDescription': app + '_zap_test_results' ,
       '-Dsonar.sources': "${env.WORKSPACE}/" ,
       '-Dsonar.projectBaseDir': "${env.WORKSPACE}/${zapResultDir}" ,
       '-Dsonar.zaproxy.reportPath': "${env.WORKSPACE}/${zapResultDir}/report.xml" ,
       '-Dsonar.qualitygate': "${zapQualityGate}" ,
       '-Dsonar.scm.enabled': 'true' ,
       '-Dsonar.log.level':'ERROR'
     ]


try {
  withEnv([
   "APP=${app}",
   "invokeBDD=${invokeBDD}",
   "RESULTSDIR=tests/acceptance/wdio/utilities/output"
  ]) {
  sh 'rm -rf ${RESULTSDIR} && mkdir -p ${RESULTSDIR}'

			createDockerContext('node48')
			sh 'pipelines/scripts/bdd.sh'

			archiveArtifacts 'tests/acceptance/wdio/utilities/output/**'
			archiveArtifacts 'zap-report/**'

   dir("${RESULTSDIR}") {
    stash     name: "BDD-${context.application}-${targetBranch}"
    includes: '*.json'
    withEnv([
     "SCENARIO_PASS_THRESHOLD=${scenarioPassThreshold}"
    ]){    sh "${env.WORKSPACE}/pipelines/scripts/bdd-pass-threshold-checker.sh"    }
   }
  }
} catch (error) {
  echo "FAILED: BDD"
   sonarRunner {
    sonarServer = sonarServerID
    preRun = "pipelines/scripts/functions"
    javaOptions = sonarJavaOptions
   }
  throw error
}
try {
sonarRunner {
  sonarServer = sonarServerID
  preRun = "pipelines/scripts/functions"
  javaOptions = sonarJavaOptions
}
} catch (error) {
echo "FAILURE: Sonar Qualification failed"
echo error.message
throw error
}
finally {
  try{
   step([	$class: 'CucumberReportPublisher',
    failedFeaturesNumber: 99999999999,
    failedScenariosNumber: 9999999999,
    failedStepsNumber: 99999999999,
    fileExcludePattern: '',
    fileIncludePattern: '**/*.json',
    jsonReportDirectory: 'tests/acceptance/wdio/utilities/output',
    parallelTesting: false,
    pendingStepsNumber: 99999999999,
    skippedStepsNumber: 99999999999,
    trendsLimit: 0,
    undefinedStepsNumber: 99999999999
   ])
  } catch(error){
   echo "FAILED; Cucumber publication. Not fatal"
  }

}

}

def publishSplunk(String targetBranch, String epoch, context, handler){
	def appname = appName(context, targetBranch)
	String  journey = context.config.journey?: 'INVALID'
	def splunkReportDir = "${context.config.splunk.reportdir}"
	echo "PUBLISH: ${this.name()} ${appname} reports to Splunk"
	sh 'rm -rf j2/bddReports'
	dir ('j2/bddReports') {
		unstash "BDD-${context.application}-${targetBranch}"
		withEnv([
			"appname=${appname}",
			"epoch=${epoch}"
		]) {   sh """ mkdir -p ${journey}/bdd/${appname}/${epoch}  && \\
                      cp *.json ${journey}/bdd/${appname}/${epoch}
                  """ }
		handler.SCP(    '*.json',
				"${splunkReportDir}")
		handler.RSYNC (journey,
				'/apps/reports/')
	}
}

String findCorrespondingBDDBranch() {
    def bddBranch
    if (env.BRANCH_NAME.startsWith('patchset')) {
        bddBranch = gerritHandler.findTargetBranch(this.findTargetCommit())
    } else if (env.BRANCH_NAME.startsWith('PR-') && env.BRANCH_NAME.endsWith('head')) {
        bddBranch = "${env.CHANGE_BRANCH}"
    } else if (env.BRANCH_NAME.startsWith('PR-')) {
        bddBranch = "${env.CHANGE_TARGET}"
    } else {
        bddBranch = "${env.BRANCH_NAME}"
    }
    return bddBranch
}

String findTargetCommit(){
	def targetCommit
	node (){
		checkout scm
		targetCommit =  sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
	}
	return targetCommit
}

String name() {
	return "BDD"
}

return this;
