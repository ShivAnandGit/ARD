@Library('workflowlib-sandbox@v2.9.7')
import com.lbg.workflow.sandbox.*

@Library('workflowlib-global@v0.2.1')
import com.lbg.workflow.global.*

properties([
	buildDiscarder(logRotator(artifactDaysToKeepStr: '30', artifactNumToKeepStr: '10', daysToKeepStr: '30', numToKeepStr: '10')),
	[$class: 'RebuildSettings', autoRebuild: true, rebuildDisabled: false]
])

def builder = 'pipelines/build/package.groovy'
def deployer = 'pipelines/deploy/application.groovy'
def unitTests = 	  [	'pipelines/tests/unit.groovy']
def staticAnalyses =  [	'pipelines/tests/sonar.groovy',
						'pipelines/tests/checkstyle.groovy' ]
						
def integrationTests = ['pipelines/tests/bdd.groovy',
						'pipelines/tests/reserved.groovy' ]					

def configuration = "pipelines/conf/job-configuration.json"
BuildHandlers handlers = new ConfigurableBuildHandlers(	builder, 
							deployer, 
							unitTests, 
							staticAnalyses, 
							integrationTests) as BuildHandlers


String notify = 'LloydsOpenBankingConsentandFraud@sapient.com,lloydscjtdevops@sapient.com'
Integer timeout = 30

//Single line invocation
invokeBuildPipelineHawk( 'ob-cnf-account-request-data-api', handlers, configuration, notify , timeout )

	
