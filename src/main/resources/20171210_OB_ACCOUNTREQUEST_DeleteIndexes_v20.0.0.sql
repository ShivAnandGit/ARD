----------------------------------------------------------------------------------------------------------------
 -- Contact: Sean wilson (swilson@sapient.com )
 -- Description: This script deletes indexes on objects in the ACCOUNTREQUEST schema
 -- Target Database location: posnpayldw005.machine.test.group
 -- Target Database name: ACCOUNTREQUEST
 -- Version number: bugfix/sprint-20
----------------------------------------------------------------------------------------------------------------

----------------------------------------------------------------------------------------------------------------
-- SQL Command Delete Indexes On Objects In ACCOUNTREQUEST Schema
----------------------------------------------------------------------------------------------------------------

DROP INDEX "ACCOUNTREQUEST"."IND_ACCT_REQUEST_EXTERNAL_ID";
DROP INDEX "ACCOUNTREQUEST"."IND_EXT_ID_CLIENT_ID_STATUS";
DROP INDEX "ACCOUNTREQUEST"."IND_CODE";