package com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception;

public final class ExceptionConstants {

    private ExceptionConstants() {
        //private constructor
    }

    public static final String ARD_API_ERR_002 = "ARD_API_ERR_002";

    public static final String ARD_API_ERR_004 = "ARD_API_ERR_004";

    public static final String ARD_API_ERR_005 = "ARD_API_ERR_005";

    public static final String ARD_API_ERR_007 = "ARD_API_ERR_007";
    
    public static final String ARD_API_ERR_008 = "ARD_API_ERR_008";

    public static final String BAD_REQUEST_INVALID_REQUEST = "Bad request::Invalid request";
    
    public static final String INCORRECT_PERMISSIONS = "User doesn't have correct permissions";

    public static final String NOT_FOUND = "Not Found";

    public static final String TXN_CORRELATION_HEADER_MISSING = "x-lbg-txn-correlation-id header is missing";

    public static final String ARD_API_ERR_100 = "ARD_API_ERR_100";

    public static final String TOO_MANY_ERRORS = "Too many errors encountered while communicating with downstream service";

    public static final String ARD_API_ERR_503 = "ARD_API_ERR_503";

}
