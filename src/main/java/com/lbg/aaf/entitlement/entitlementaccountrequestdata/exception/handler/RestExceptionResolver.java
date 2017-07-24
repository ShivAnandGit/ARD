package com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.handler;

import com.core.lbg.security.annotation.exception.ForbiddenException;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.*;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.AccountRequestDataConstant;
import com.lbg.ob.logger.Logger;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.ExceptionConstants.*;
import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.AccountRequestDataConstant.X_LBG_TXN_CORRELATION_ID;

@ControllerAdvice
public class RestExceptionResolver extends ResponseEntityExceptionHandler {


    @Autowired
    private Logger exceptionLogger;

    @ExceptionHandler(value = { InvalidRequestException.class })
    protected ResponseEntity<Object> handleInvalidRequestException(BaseException ex, WebRequest request) {
        ErrorData errorData = ex.getErrorData();
        errorData.setStatusCode(Long.valueOf(HttpStatus.BAD_REQUEST.toString()));
        exceptionLogger.logException(request.getHeader(X_LBG_TXN_CORRELATION_ID), (InvalidRequestException)ex);
        return handleExceptionInternal(ex, errorData, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {EntitlementUpdateFailedException.class})
    protected ResponseEntity<Object> handleInternalServerErrors(BaseException ex, WebRequest request) {
        ErrorData errorData = ex.getErrorData();
        errorData.setStatusCode(Long.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.toString()));
        exceptionLogger.logException(request.getHeader(X_LBG_TXN_CORRELATION_ID), (EntitlementUpdateFailedException)ex);
        return handleExceptionInternal(ex, errorData, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {RecordNotFoundException.class})
    protected ResponseEntity<Object> handleNotFoundException(BaseException ex, WebRequest request) {
        ErrorData errorData = ex.getErrorData();
        errorData.setStatusCode(Long.valueOf(HttpStatus.NOT_FOUND.toString()));
        exceptionLogger.logException(request.getHeader(X_LBG_TXN_CORRELATION_ID), (RecordNotFoundException)ex);
        return handleExceptionInternal(ex, errorData, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException ex, WebRequest request) {
        ErrorData errorData = new ErrorData((long) HttpStatus.FORBIDDEN.value(), ExceptionConstants.ARD_API_ERR_008, ExceptionConstants.INCORRECT_PERMISSIONS);
        return handleExceptionInternal(ex, errorData, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = ex.getMessage();
        String correlationId = request.getHeader(X_LBG_TXN_CORRELATION_ID);
        if(message.contains(X_LBG_TXN_CORRELATION_ID)) {
            correlationId = TXN_CORRELATION_HEADER_MISSING;
        }
        exceptionLogger.logException(correlationId, (ServletRequestBindingException)ex);
        return this.handleExceptionInternal(ex, new ErrorData(Long.valueOf(HttpStatus.BAD_REQUEST.toString()), ARD_API_ERR_002, message), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Object> handleHysterixException(ResourceAccessException ex, WebRequest request) {
        ErrorData errorData = new ErrorData(Long.valueOf(HttpStatus.SERVICE_UNAVAILABLE.toString()), ARD_API_ERR_503, AccountRequestDataConstant.TIME_OUT_MSG);
        exceptionLogger.logException(request.getHeader(X_LBG_TXN_CORRELATION_ID), ex.getCause());
        exceptionLogger.logFatal(request.getHeader(X_LBG_TXN_CORRELATION_ID), (null!=ex.getCause())?ex.getCause().getMessage():null);
        return handleExceptionInternal(ex, errorData, new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(HystrixRuntimeException.class)
    public ResponseEntity<Object> hysterixExceptionHandler(HystrixRuntimeException ex, WebRequest request) {
        ErrorData errorData = new ErrorData(Long.valueOf(HttpStatus.SERVICE_UNAVAILABLE.toString()), ARD_API_ERR_503, AccountRequestDataConstant.TIME_OUT_MSG);
        if(ex.getFailureType().equals(HystrixRuntimeException.FailureType.SHORTCIRCUIT)) {
            errorData.setMessage(TOO_MANY_ERRORS);
            errorData.setCode(ARD_API_ERR_100);
        }
        exceptionLogger.logException(request.getHeader(X_LBG_TXN_CORRELATION_ID), ex.getCause());
        exceptionLogger.logFatal(request.getHeader(X_LBG_TXN_CORRELATION_ID), ex.getCause().getMessage());
        return handleExceptionInternal(ex, errorData, new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }
}
