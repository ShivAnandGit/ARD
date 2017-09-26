package com.lbg.ob.aisp.accountrequestdata.util;

public final class AccountRequestDataConstant {


    /*
         * private constructor
         */
    private AccountRequestDataConstant() {
        // private Constructor
    }

    public static final String SWAGGER_GROUPNAME = "entitlementaccountrequestdata";
    public static final String SWAGGER_PATH = "/v1/.*";
    public static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String TIMEZONE = "UTC";
    public static final String X_LBG_INTERNAL_USER_ID = "x-lbg-internal-user-id";
    public static final String X_LBG_CLIENT_ID = "x-lbg-client-id";
    public static final String X_LBG_INTERNAL_USER_ROLE = "x-lbg-internal-user-role";
    public static final String X_LBG_TXN_CORRELATION_ID = "x-lbg-txn-correlation-id";
    public static final String AUTHORISED = "Authorised";
    public static final String REJECTED = "Rejected";
    public static final String AWAITING_AUTHORISATION = "AwaitingAuthorisation";
    public static final String REVOKED = "Revoked";
    public static final String X_FAPI_INTERACTION_ID = "x-fapi-interaction-id";
    public static final String X_FAPI_FINANCIAL_ID = "x-fapi-financial-id";
    public static final String TIME_OUT_MSG = "Timed out while connecting to downstream service";

}
