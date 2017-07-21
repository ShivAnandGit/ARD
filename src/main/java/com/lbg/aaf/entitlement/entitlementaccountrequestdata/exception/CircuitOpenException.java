package com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.handler.ErrorData;

/**
 * Created by pbabb1 on 7/14/17.
 */
public class CircuitOpenException extends BaseException {

    private static final long serialVersionUID = 1L;

    public CircuitOpenException(String message) {
        this(new ErrorData(null, null, message));
    }


    public CircuitOpenException(ErrorData errorData) {
        super(errorData);
    }
}
