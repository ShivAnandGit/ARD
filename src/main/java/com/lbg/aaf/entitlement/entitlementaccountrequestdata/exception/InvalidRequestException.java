package com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.handler.ErrorData;

public class InvalidRequestException extends BaseException {

    public InvalidRequestException(ErrorData errorData) {
        super(errorData);
    }

    public InvalidRequestException(String message) {
        this(new ErrorData(null, null, message));
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(new ErrorData(null, null, message), cause);
    }

    public InvalidRequestException(String message, String code) {
        super(new ErrorData(null, code, message));
    }

    public InvalidRequestException(String message, String code, Throwable cause) {
        super(new ErrorData(null, code, message), cause);
    }

}
