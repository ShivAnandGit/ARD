package com.lbg.ob.aisp.accountrequestdata.exception;

import com.lbg.ob.aisp.accountrequestdata.exception.handler.ErrorData;

public class BaseException extends RuntimeException {
    private ErrorData errorData;

    public BaseException() {
        //default constructor
    }

    public BaseException(ErrorData errorData) {
        super(errorData.getMessage());
        this.errorData = errorData;
    }

    public BaseException(ErrorData errorData, Throwable cause) {
        super(errorData.getMessage(), cause);
        this.errorData = errorData;
    }

    public ErrorData getErrorData() {
        return errorData;
    }

    public void setErrorData(ErrorData errorData) {
        this.errorData = errorData;
    }
}
