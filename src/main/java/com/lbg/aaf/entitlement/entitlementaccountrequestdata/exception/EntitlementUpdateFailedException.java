package com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.handler.ErrorData;

public class EntitlementUpdateFailedException extends BaseException {
    public EntitlementUpdateFailedException(ErrorData errorData) {
        super(errorData);
    }

    public EntitlementUpdateFailedException(ErrorData errorData, Throwable cause) {
        super(errorData, cause);
    }

    public EntitlementUpdateFailedException(String message) {
        super(new ErrorData(message));
    }

    public EntitlementUpdateFailedException(String message, Throwable cause) {
        super(new ErrorData(message), cause);
    }

    public EntitlementUpdateFailedException(String message, String code) {
        super(new ErrorData(message, code));
    }

    public EntitlementUpdateFailedException(String message, String code, Throwable cause) {
        super(new ErrorData(message, code), cause);
    }
}
