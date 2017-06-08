/*
 * Author: Abhay Chrungoo <abhay@ziraffe.io>
 * Contributing HOWTO: TODO
 */

 /*
  * Invocation up top. Should function as a standalone Jenkinsfile
  */

 invokeRegression (args)
 
 /*
  * Avoid nested workspace allocations.
  * eg: dont invoke node(){ } in a utility method
  * Can call other pipelines handlers/scripts if needed
  */
 
 def invokeRegression(args){
	 node(){
		 /*
		  * Implementation
		  */
		 
	 }
 }
 
 def utilityMethod(args){
	 /*
	  * Implementation
	  */
 }