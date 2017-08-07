def runTest(String targetBranch, context) {
/*
     * Lightweight operations that dont require a workspace.
     * eg: variable manipulations.
     */
node(){
    def mavenPom = context.config.maven.pom ?: 'pom.xml'
    def mavenGoals = context.config.maven.package_goals ?: '-U clean clean package -DskipTests=true'
    //String  veracodeCredentials = context.config.veracode.credentials ?: 'ob-veracode-api-user2'



        checkout scm
        try {
            sh """
                source ${WORKSPACE}/pipelines/scripts/functions && \
                mvn ${mavenGoals} -f ${mavenPom}
                """
            //sh "/apps/tools/ant/bin/ant -f ${WORKSPACE}/pipelines/conf/build.xml"
            stash name: "veracode", includes: 'target*//*.war'



        } catch (error) {
            echo "FAILURE: Failure in veracode packaging"
            echo error.message
            throw error
        } finally {
            //WsCleanup
            step([$class: 'WsCleanup', notFailBuild: true])
        }

}
}


def uploadVeracode(String targetBranch, context) {
def veracodeCredentials = context.config.veracode.credentials ?: 'ob-veracode-api-user2'
def veracodeID =  context.config.veracode.id ?: ''

    withCredentials([
      usernamePassword(credentialsId: veracodeCredentials,
      passwordVariable: 'API_PASSWORD',
      usernameVariable: 'API_USERNAME')
    ]){
    withEnv([
     "APP_ID=${veracodeID}",
    ]) {
        checkout scm
        unstash 'veracode'
        try {
            sh """
                source ${WORKSPACE}/pipelines/scripts/functions && \
                source ${WORKSPACE}/pipelines/scripts/veracode.sh target/
                """
            stash name: "veracodereport", includes: 'veracodeResults/*.xml'


        } catch (error) {
            echo "FAILURE: veracode failed"
            echo error.message
            throw error
        } finally {
            archiveArtifacts allowEmptyArchive: true,
                    artifacts: 'veracodeResults/',
                    fingerprint: true,
                    onlyIfSuccessful: false
              //Publish anything thats needed


            //WsCleanup
            step([$class: 'WsCleanup', notFailBuild: true])
          }
        }
    }

}


def publishSplunk(String targetBranch, String epoch, context, handler){
                       def appname = appName(context.application, targetBranch)
                       String  journey = context.config.journey?: 'INVALID'
                       def splunkReportDir = "${context.config.splunk.reportdir}"
                       echo "PUBLISH: ${this.name()} ${appname} reports to Splunk"
                       sh 'rm -rf j2/vcReports'
                       dir ('j2/vcReports') {
                           unstash "veracodereport"
                           sh 'ls -lR'
                           withEnv([
                               "appname=${appname}",
                               "epoch=${epoch}"
                           ]) {   sh """ mkdir -p ${journey}/${appname}/${epoch}  && \\
                                          cp veracodeResults/*.xml ${journey}/${appname}/${epoch}
                                      """ }
                           splunkPublisher.SCP(    'veracodeResults/*.xml',
                                   "${splunkReportDir}")
                           splunkPublisher.RSYNC (journey,
                                   '/apps/reports/')
                       }
                    }

String name() {
    return "VERACODE_TESTS"
}

return this;
