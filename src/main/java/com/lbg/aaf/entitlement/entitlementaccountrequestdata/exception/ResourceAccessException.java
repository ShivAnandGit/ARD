package com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception;


import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.handler.ErrorData;

/**
 * Created by pbabb1 on 7/18/17.
 */
public class ResourceAccessException extends BaseException {

    public ResourceAccessException(ErrorData errorData) {
        super(errorData);
    }

    public ResourceAccessException(ErrorData errorData, Throwable t) {
        super(errorData, t);
    }

}
