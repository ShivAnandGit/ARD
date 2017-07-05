package com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.handler;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.BaseException;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.EntitlementUpdateFailedException;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.InvalidRequestException;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.RecordNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.ExceptionConstants.ARD_API_ERR_002;

@ControllerAdvice
public class RestExceptionResolver extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { InvalidRequestException.class })
    protected ResponseEntity<Object> handleInvalidRequestException(BaseException ex, WebRequest request) {
        ErrorData errorData = ex.getErrorData();
        errorData.setStatusCode(Long.valueOf(HttpStatus.BAD_REQUEST.toString()));
        return handleExceptionInternal(ex, errorData, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = { EntitlementUpdateFailedException.class })
    protected ResponseEntity<Object> handleInternalServerErrors(BaseException ex, WebRequest request) {
        ErrorData errorData = ex.getErrorData();
        errorData.setStatusCode(Long.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.toString()));
        return handleExceptionInternal(ex, errorData, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = { RecordNotFoundException.class })
    protected ResponseEntity<Object> handleNotFoundException(BaseException ex, WebRequest request) {
        ErrorData errorData = ex.getErrorData();
        errorData.setStatusCode(Long.valueOf(HttpStatus.NOT_FOUND.toString()));
        return handleExceptionInternal(ex, errorData, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }


    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = ex.getMessage();
        return this.handleExceptionInternal(ex, new ErrorData(Long.valueOf(HttpStatus.BAD_REQUEST.toString()), ARD_API_ERR_002, message), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
