package com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.handler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pbabb1 on 6/30/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ErrorData {
    private Long statusCode;

    private String code;

    private String message;

    public ErrorData() {
        //default constructor
    }

    public ErrorData(String message) {
        this(null, null, message);
    }

    public ErrorData(String message, String code) {
        this(null, code, message);
    }

    public ErrorData(Long statusCode, String code, String message) {
        this.code = code;
        this.statusCode = statusCode;
        this.message = message;
    }

    @JsonIgnore
    public Long getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Long statusCode) {
        this.statusCode = statusCode;
    }

    @JsonIgnore
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonIgnore
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("error")
    public Map<String, Object> getError() {
        Map<String, Object> error = new HashMap<>();
        error.put("code", getCode());
        error.put("statusCode", getStatusCode());
        error.put("message", getMessage());
        return error;
    }
}
