----------------------------------------------------------------------------------------------------------------
 -- Contact: Puneet Babbar (pbabbar2@sapient.com )
 -- Description: This script will DROP all objects needed by the Account request data API in the ACCOUNT schema. this script can be used to Wipe clean the schema.
 -- Target Database location: posnpayldw005.machine.test.group
 -- Target Database name: ACCOUNTREQUEST
 ----------------------------------------------------------------------------------------------------------------



-------------------------------------
-- SQL Command to drop sequence ACCT_REQUEST_SEQ
-------------------------------------

drop sequence "ACCOUNTREQUEST"."ACCT_REQUEST_SEQ";

-------------------------------------
-- SQL Command to drop sequence ACCT_REQUEST_STATUS_HIST_SEQ
-------------------------------------

drop sequence "ACCOUNTREQUEST"."ACCT_REQUEST_STATUS_HIST_SEQ";

-------------------------------------
-- SQL Command to drop sequence PROVIDER_PERMISSION_SEQ
-------------------------------------

drop sequence "ACCOUNTREQUEST"."PROVIDER_PERMISSION_SEQ";

-------------------------------------
-- SQL Command to drop table ACCT_REQUEST_STATUS_HIST
-------------------------------------

DROP TABLE "ACCOUNTREQUEST"."ACCT_REQUEST_STATUS_HIST";

-------------------------------------
-- SQL Command to drop table ACCT_REQUEST
-------------------------------------

DROP TABLE "ACCOUNTREQUEST"."ACCT_REQUEST";

-------------------------------------
-- SQL Command to drop table REF_ACCT_REQUEST_STATUS
-------------------------------------

DROP TABLE "ACCOUNTREQUEST"."REF_ACCT_REQUEST_STATUS";

-------------------------------------
-- SQL Command to drop table PROVIDER_PERMISSION
-------------------------------------

DROP TABLE "ACCOUNTREQUEST"."PROVIDER_PERMISSION";
