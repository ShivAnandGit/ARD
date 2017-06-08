/*
 * Author: Abhay Chrungoo <abhay@ziraffe.io>
 * Contributing HOWTO: TODO
 */
def runTest(String targetBranch, context){
	node() {
		this.runTestHandler(targetBranch, context)
	}
}
def runTestHandler(String targetBranch, context){
	echo "RESERVED: For future use."
}
def publishSplunk(String targetBranch, String epoch, context, handler){
	/*
	 * Your implementation for publishing the reports of the runTest method to splunk.
	 * Can use library functions to make it easier
	 * handler also provides utility methods to fulfil this task.
	 * In this case handler.SCP and handler.RSYNC  are available
	 * Refer to workflowlib-sandbox for details
	 */
}
String name(){
	return "Reserved"
}
return this;
