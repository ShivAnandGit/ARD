----------------------------------------------------------------------------------------------------------------
 -- Contact: Sean wilson (swilson@sapient.com )
 -- Description: This script create indexes on objects in the ACCOUNTREQUEST schema
 -- Target Database location: posnpayldw005.machine.test.group
 -- Target Database name: ACCOUNTREQUEST
 -- Version number: bugfix/sprint-20
----------------------------------------------------------------------------------------------------------------

----------------------------------------------------------------------------------------------------------------
-- SQL Command Create Indexes On Objects In ACCOUNTREQUEST Schema
----------------------------------------------------------------------------------------------------------------

CREATE UNIQUE INDEX "ACCOUNTREQUEST"."IND_ACCT_REQUEST_EXTERNAL_ID" ON "ACCOUNTREQUEST"."ACCT_REQUEST"(ACCOUNT_REQUEST_EXTERNAL_ID);
CREATE INDEX "ACCOUNTREQUEST"."IND_EXT_ID_CLIENT_ID_STATUS" ON "ACCOUNTREQUEST"."ACCT_REQUEST"(ACCOUNT_REQUEST_EXTERNAL_ID,PROVIDER_CLIENT_ID,ACCOUNT_REQUEST_STATUS);
CREATE INDEX "ACCOUNTREQUEST"."IND_CODE" ON "ACCOUNTREQUEST"."PROVIDER_PERMISSION"(CODE);