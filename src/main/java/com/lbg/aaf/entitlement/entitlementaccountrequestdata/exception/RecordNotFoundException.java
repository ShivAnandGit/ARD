package com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.handler.ErrorData;

public class RecordNotFoundException extends BaseException {
    public RecordNotFoundException(ErrorData errorData) {
        super(errorData);
    }

    public RecordNotFoundException(String message) {
        super();
        ErrorData errorData = new ErrorData();
        errorData.setMessage(message);
        this.setErrorData(errorData);
    }

    public RecordNotFoundException(String message, String code) {
        super(new ErrorData(null, code, message));
    }
}

