package com.lbg.ob.aisp.accountrequestdata.exception;


import com.lbg.ob.aisp.accountrequestdata.exception.handler.ErrorData;

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
