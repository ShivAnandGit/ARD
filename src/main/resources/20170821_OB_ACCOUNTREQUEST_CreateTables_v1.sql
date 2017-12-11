----------------------------------------------------------------------------------------------------------------
 -- Contact: Puneet Babbar (pbabbar2@sapient.com )
 -- Description: This script will create all objects needed by the Account request data API in the ACCOUNT schema
 -- Target Database location: posnpayldw005.machine.test.group
 -- Target Database name: ACCOUNTREQUEST
----------------------------------------------------------------------------------------------------------------

--------------------------------------------------------
--  DDL for Table ACCT_REQUEST_STATUS_HIST
--------------------------------------------------------

  CREATE TABLE "ACCOUNTREQUEST"."ACCT_REQUEST_STATUS_HIST" 
   (	"ACCT_REQUEST_STATUS_HIST_ID" NUMBER(38,0), 
	"ACCT_REQUEST_ID" NUMBER(38,0), 
	"STATUS_UPDATED_DATE_TIME" TIMESTAMP (6), 
	"ACCOUNT_REQUEST_STATUS" VARCHAR2(50 BYTE), 
	"STATUS_UPDATED_BY_ROLE" VARCHAR2(50 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table ACCT_REQUEST
--------------------------------------------------------

  CREATE TABLE "ACCOUNTREQUEST"."ACCT_REQUEST" 
   (	"ACCT_REQUEST_ID" NUMBER(38,0), 
	"ACCOUNT_REQUEST_EXTERNAL_ID" VARCHAR2(40 BYTE), 
	"PROVIDER_CLIENT_ID" VARCHAR2(50 BYTE), 
	"CREATED_DATE_TIME" TIMESTAMP (6), 
	"ACCOUNT_REQUEST_STATUS" VARCHAR2(50 BYTE), 
	"ACCOUNT_REQUEST_JSON_STRING" CLOB, 
	"FAPI_FINANCIAL_ID" VARCHAR2(50 BYTE), 
	"ENTITLEMENT_ID" NUMBER(38,0)
   ) ;
--------------------------------------------------------
--  DDL for Table PROVIDER_PERMISSION
--------------------------------------------------------

  CREATE TABLE "ACCOUNTREQUEST"."PROVIDER_PERMISSION" 
   (	"PROVIDER_PERMISSION_ID" NUMBER(38,0), 
	"CODE" VARCHAR2(50 BYTE), 
	"DESCRIPTION" VARCHAR2(255 BYTE), 
	"SECTION_HEADER" VARCHAR2(30 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table REF_ACCT_REQUEST_STATUS
--------------------------------------------------------

  CREATE TABLE "ACCOUNTREQUEST"."REF_ACCT_REQUEST_STATUS" 
   (	"CODE" VARCHAR2(50 BYTE), 
	"DESCRIPTION" VARCHAR2(255 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Index PK_ACCT_REQUEST_STATUS_HIST
--------------------------------------------------------

  CREATE UNIQUE INDEX "ACCOUNTREQUEST"."PK_ACCT_REQUEST_STATUS_HIST" ON "ACCOUNTREQUEST"."ACCT_REQUEST_STATUS_HIST" ("ACCT_REQUEST_STATUS_HIST_ID") ;
--------------------------------------------------------
--  DDL for Index PK_ACCT_REQUEST
--------------------------------------------------------

  CREATE UNIQUE INDEX "ACCOUNTREQUEST"."PK_ACCT_REQUEST" ON "ACCOUNTREQUEST"."ACCT_REQUEST" ("ACCT_REQUEST_ID") ;
--------------------------------------------------------
--  DDL for Index PK_PROVIDER_PERMISSION
--------------------------------------------------------

  CREATE UNIQUE INDEX "ACCOUNTREQUEST"."PK_PROVIDER_PERMISSION" ON "ACCOUNTREQUEST"."PROVIDER_PERMISSION" ("PROVIDER_PERMISSION_ID") ;
--------------------------------------------------------
--  DDL for Index PK_REF_ACCT_REQUEST_STATUS
--------------------------------------------------------

  CREATE UNIQUE INDEX "ACCOUNTREQUEST"."PK_REF_ACCT_REQUEST_STATUS" ON "ACCOUNTREQUEST"."REF_ACCT_REQUEST_STATUS" ("CODE") ;

--------------------------------------------------------
--  Constraints for Table ACCT_REQUEST
--------------------------------------------------------

  ALTER TABLE "ACCOUNTREQUEST"."ACCT_REQUEST" ADD CONSTRAINT "PK_ACCT_REQUEST" PRIMARY KEY ("ACCT_REQUEST_ID") ENABLE;
  ALTER TABLE "ACCOUNTREQUEST"."ACCT_REQUEST" MODIFY ("FAPI_FINANCIAL_ID" NOT NULL ENABLE);
  ALTER TABLE "ACCOUNTREQUEST"."ACCT_REQUEST" MODIFY ("ACCOUNT_REQUEST_JSON_STRING" NOT NULL ENABLE);
  ALTER TABLE "ACCOUNTREQUEST"."ACCT_REQUEST" MODIFY ("ACCOUNT_REQUEST_STATUS" NOT NULL ENABLE);
  ALTER TABLE "ACCOUNTREQUEST"."ACCT_REQUEST" MODIFY ("CREATED_DATE_TIME" NOT NULL ENABLE);
  ALTER TABLE "ACCOUNTREQUEST"."ACCT_REQUEST" MODIFY ("PROVIDER_CLIENT_ID" NOT NULL ENABLE);
  ALTER TABLE "ACCOUNTREQUEST"."ACCT_REQUEST" MODIFY ("ACCOUNT_REQUEST_EXTERNAL_ID" NOT NULL ENABLE);
  ALTER TABLE "ACCOUNTREQUEST"."ACCT_REQUEST" MODIFY ("ACCT_REQUEST_ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table PROVIDER_PERMISSION
--------------------------------------------------------

  ALTER TABLE "ACCOUNTREQUEST"."PROVIDER_PERMISSION" ADD CONSTRAINT "PK_PROVIDER_PERMISSION" PRIMARY KEY ("PROVIDER_PERMISSION_ID") ENABLE;
  ALTER TABLE "ACCOUNTREQUEST"."PROVIDER_PERMISSION" MODIFY ("SECTION_HEADER" NOT NULL ENABLE);
  ALTER TABLE "ACCOUNTREQUEST"."PROVIDER_PERMISSION" MODIFY ("DESCRIPTION" NOT NULL ENABLE);
  ALTER TABLE "ACCOUNTREQUEST"."PROVIDER_PERMISSION" MODIFY ("CODE" NOT NULL ENABLE);
  ALTER TABLE "ACCOUNTREQUEST"."PROVIDER_PERMISSION" MODIFY ("PROVIDER_PERMISSION_ID" NOT NULL ENABLE);

--------------------------------------------------------
--   REF-DATA for PROVIDER_PERMISSION
--------------------------------------------------------

Insert into PROVIDER_PERMISSION (PROVIDER_PERMISSION_ID,CODE,DESCRIPTION,SECTION_HEADER) values (1,'ReadAccountsBasic','Nickname of your account','Account details');
Insert into PROVIDER_PERMISSION (PROVIDER_PERMISSION_ID,CODE,DESCRIPTION,SECTION_HEADER) values (2,'ReadAccountsDetail','Account names, sort code and account number','Account details');
Insert into PROVIDER_PERMISSION (PROVIDER_PERMISSION_ID,CODE,DESCRIPTION,SECTION_HEADER) values (3,'ReadBalances','Balance and available funds','Account details');
Insert into PROVIDER_PERMISSION (PROVIDER_PERMISSION_ID,CODE,DESCRIPTION,SECTION_HEADER) values (4,'ReadProducts','Account type, rates and charges','Account details');
Insert into PROVIDER_PERMISSION (PROVIDER_PERMISSION_ID,CODE,DESCRIPTION,SECTION_HEADER) values (5,'ReadTransactionsBasic','Amount and date of each transaction','Transactions');
Insert into PROVIDER_PERMISSION (PROVIDER_PERMISSION_ID,CODE,DESCRIPTION,SECTION_HEADER) values (6,'ReadTransactionsCredits','Amount and date of each transaction','Transactions');
Insert into PROVIDER_PERMISSION (PROVIDER_PERMISSION_ID,CODE,DESCRIPTION,SECTION_HEADER) values (7,'ReadTransactionsDebits','Amount and date of each transaction','Transactions');
Insert into PROVIDER_PERMISSION (PROVIDER_PERMISSION_ID,CODE,DESCRIPTION,SECTION_HEADER) values (8,'ReadTransactionsDetail','Full statement details of each transaction','Transactions');
Insert into PROVIDER_PERMISSION (PROVIDER_PERMISSION_ID,CODE,DESCRIPTION,SECTION_HEADER) values (9,'ReadBeneficiariesBasic','Payment references for your saved payees','Regular payments and payees');
Insert into PROVIDER_PERMISSION (PROVIDER_PERMISSION_ID,CODE,DESCRIPTION,SECTION_HEADER) values (10,'ReadBeneficiariesDetail','Payment references and account details of your saved payees','Regular payments and payees');
Insert into PROVIDER_PERMISSION (PROVIDER_PERMISSION_ID,CODE,DESCRIPTION,SECTION_HEADER) values (11,'ReadDirectDebits','Summary of your direct debits','Regular payments and payees');
Insert into PROVIDER_PERMISSION (PROVIDER_PERMISSION_ID,CODE,DESCRIPTION,SECTION_HEADER) values (12,'ReadStandingOrdersBasic','Summary of your standing orders','Regular payments and payees');
Insert into PROVIDER_PERMISSION (PROVIDER_PERMISSION_ID,CODE,DESCRIPTION,SECTION_HEADER) values (13,'ReadStandingOrdersDetail','Summary of your standing orders, including payee account details','Regular payments and payees');

--------------------------------------------------------
--  Constraints for Table REF_ACCT_REQUEST_STATUS
--------------------------------------------------------

  ALTER TABLE "ACCOUNTREQUEST"."REF_ACCT_REQUEST_STATUS" ADD CONSTRAINT "PK_REF_ACCT_REQUEST_STATUS" PRIMARY KEY ("CODE") ENABLE;
  ALTER TABLE "ACCOUNTREQUEST"."REF_ACCT_REQUEST_STATUS" MODIFY ("DESCRIPTION" NOT NULL ENABLE);
  ALTER TABLE "ACCOUNTREQUEST"."REF_ACCT_REQUEST_STATUS" MODIFY ("CODE" NOT NULL ENABLE);

--------------------------------------------------------
--   REF-DATA for REF_ACCT_REQUEST_STATUS
--------------------------------------------------------

INSERT INTO "ACCOUNTREQUEST"."REF_ACCT_REQUEST_STATUS" (CODE, DESCRIPTION) VALUES ('AwaitingAuthorisation', 'AwaitingAuthorisation');
INSERT INTO "ACCOUNTREQUEST"."REF_ACCT_REQUEST_STATUS" (CODE, DESCRIPTION) VALUES ('Authorised', 'Authorised');
INSERT INTO "ACCOUNTREQUEST"."REF_ACCT_REQUEST_STATUS" (CODE, DESCRIPTION) VALUES ('Rejected', 'Rejected');
INSERT INTO "ACCOUNTREQUEST"."REF_ACCT_REQUEST_STATUS" (CODE, DESCRIPTION) VALUES ('Revoked', 'Revoked');

--------------------------------------------------------
--  Constraints for Table ACCT_REQUEST_STATUS_HIST
--------------------------------------------------------

  ALTER TABLE "ACCOUNTREQUEST"."ACCT_REQUEST_STATUS_HIST" ADD CONSTRAINT "PK_ACCT_REQUEST_STATUS_HIST" PRIMARY KEY ("ACCT_REQUEST_STATUS_HIST_ID") ENABLE;
  ALTER TABLE "ACCOUNTREQUEST"."ACCT_REQUEST_STATUS_HIST" MODIFY ("STATUS_UPDATED_BY_ROLE" NOT NULL ENABLE);
  ALTER TABLE "ACCOUNTREQUEST"."ACCT_REQUEST_STATUS_HIST" MODIFY ("ACCOUNT_REQUEST_STATUS" NOT NULL ENABLE);
  ALTER TABLE "ACCOUNTREQUEST"."ACCT_REQUEST_STATUS_HIST" MODIFY ("STATUS_UPDATED_DATE_TIME" NOT NULL ENABLE);
  ALTER TABLE "ACCOUNTREQUEST"."ACCT_REQUEST_STATUS_HIST" MODIFY ("ACCT_REQUEST_ID" NOT NULL ENABLE);
  ALTER TABLE "ACCOUNTREQUEST"."ACCT_REQUEST_STATUS_HIST" MODIFY ("ACCT_REQUEST_STATUS_HIST_ID" NOT NULL ENABLE);
  ALTER TABLE "ACCOUNTREQUEST"."ACCT_REQUEST_STATUS_HIST" ADD CONSTRAINT "FK_ACT_REQ_STA_HIS_ACT_ID" FOREIGN KEY ("ACCT_REQUEST_ID") REFERENCES "ACCOUNTREQUEST"."ACCT_REQUEST" ("ACCT_REQUEST_ID") ENABLE;
  ALTER TABLE "ACCOUNTREQUEST"."ACCT_REQUEST_STATUS_HIST" ADD CONSTRAINT "FK_ACCT_REQ_STATUS_ACCT_STATUS" FOREIGN KEY ("ACCOUNT_REQUEST_STATUS") REFERENCES "ACCOUNTREQUEST"."REF_ACCT_REQUEST_STATUS" ("CODE") ENABLE;

--------------------------------------------------------
--  DDL for Sequence ACCT_REQUEST_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "ACCOUNTREQUEST"."ACCT_REQUEST_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 ORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence ACCT_REQUEST_STATUS_HIST_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "ACCOUNTREQUEST"."ACCT_REQUEST_STATUS_HIST_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 ORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence PROVIDER_PERMISSION_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "ACCOUNTREQUEST"."PROVIDER_PERMISSION_SEQ"  MINVALUE 1 MAXVALUE 10000 INCREMENT BY 1 START WITH 1 CACHE 20 ORDER  CYCLE ;
