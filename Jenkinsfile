@Library('workflowlib-sandbox@v4.8')
import com.lbg.workflow.sandbox.*

properties(defaultBuildJobProperties())

BuildHandlers handlers = new CWABuildHandlers()
def appname = "ob-aisp-account-request-data-api"
def configuration = "pipelines/conf/job-configuration.json"
def distroList = "LloydsOpenBankingConsentandFraud@sapient.com,lloydscjtdevops@sapient.com"
Integer timeout = "60"

invokeBuildPipelineHawk(appname, handlers, configuration, distroList, timeout)
