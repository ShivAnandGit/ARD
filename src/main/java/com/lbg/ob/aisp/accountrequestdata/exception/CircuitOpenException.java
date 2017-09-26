package com.lbg.ob.aisp.accountrequestdata.exception;

import com.lbg.ob.aisp.accountrequestdata.exception.handler.ErrorData;

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
