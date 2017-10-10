package com.lbg.ob.aisp.accountrequestdata.exception.handler;

import com.core.lbg.security.annotation.exception.ForbiddenException;
import com.lbg.ob.aisp.accountrequestdata.exception.BaseException;
import com.lbg.ob.aisp.accountrequestdata.exception.EntitlementUpdateFailedException;
import com.lbg.ob.aisp.accountrequestdata.exception.ExceptionConstants;
import com.lbg.ob.aisp.accountrequestdata.exception.InvalidRequestException;
import com.lbg.ob.aisp.accountrequestdata.exception.RecordNotFoundException;
import com.lbg.ob.aisp.accountrequestdata.exception.ResourceAccessException;
import com.lbg.ob.aisp.accountrequestdata.util.AccountRequestDataConstant;
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

import static com.lbg.ob.aisp.accountrequestdata.exception.ExceptionConstants.ARD_API_ERR_503;
import static com.lbg.ob.aisp.accountrequestdata.util.AccountRequestDataConstant.X_LBG_TXN_CORRELATION_ID;

@ControllerAdvice
public class RestExceptionResolver extends ResponseEntityExceptionHandler {


    @Autowired
    private Logger exceptionLogger;

    @ExceptionHandler(value = { InvalidRequestException.class })
    protected ResponseEntity<ErrorData> handleInvalidRequestException(BaseException ex, WebRequest request) {
        ErrorData errorData = ex.getErrorData();
        errorData.setStatusCode(Long.valueOf(HttpStatus.BAD_REQUEST.toString()));
        exceptionLogger.exception(request.getHeader(X_LBG_TXN_CORRELATION_ID), (InvalidRequestException)ex);
        return new ResponseEntity<>(errorData, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {EntitlementUpdateFailedException.class})
    protected ResponseEntity<ErrorData> handleInternalServerErrors(BaseException ex, WebRequest request) {
        ErrorData errorData = ex.getErrorData();
        errorData.setStatusCode(Long.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.toString()));
        exceptionLogger.exception(request.getHeader(X_LBG_TXN_CORRELATION_ID), (EntitlementUpdateFailedException)ex);
        return new ResponseEntity<>(errorData, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {RecordNotFoundException.class})
    protected ResponseEntity<ErrorData> handleNotFoundException(BaseException ex, WebRequest request) {
        ErrorData errorData = ex.getErrorData();
        errorData.setStatusCode(Long.valueOf(HttpStatus.NOT_FOUND.toString()));
        exceptionLogger.exception(request.getHeader(X_LBG_TXN_CORRELATION_ID), (RecordNotFoundException)ex);
        return new ResponseEntity<>(errorData, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorData> handleForbiddenException(ForbiddenException ex, WebRequest request) {
        ErrorData errorData = new ErrorData((long) HttpStatus.FORBIDDEN.value(), ExceptionConstants.ARD_API_ERR_008, ExceptionConstants.INCORRECT_PERMISSIONS);
        return new ResponseEntity<>(errorData, HttpStatus.FORBIDDEN);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = ex.getMessage();
        String correlationId = request.getHeader(X_LBG_TXN_CORRELATION_ID);
        if(message.contains(X_LBG_TXN_CORRELATION_ID)) {
            correlationId = ExceptionConstants.TXN_CORRELATION_HEADER_MISSING;
        }
        exceptionLogger.exception(correlationId, (ServletRequestBindingException)ex);
        return this.handleExceptionInternal(ex, new ErrorData(Long.valueOf(HttpStatus.BAD_REQUEST.toString()), ExceptionConstants.ARD_API_ERR_002, message), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorData> handleResourceAccessException(ResourceAccessException ex, WebRequest request) {
        ErrorData errorData = ex.getErrorData();
        exceptionLogger.exception(request.getHeader(X_LBG_TXN_CORRELATION_ID), ex.getCause());
        exceptionLogger.fatal(request.getHeader(X_LBG_TXN_CORRELATION_ID), (null!=ex.getCause())?ex.getCause().getMessage():null);
        return new ResponseEntity<>(errorData, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(HystrixRuntimeException.class)
    public ResponseEntity<ErrorData> hysterixExceptionHandler(HystrixRuntimeException ex, WebRequest request) {
        ErrorData errorData = new ErrorData(Long.valueOf(HttpStatus.SERVICE_UNAVAILABLE.toString()), ARD_API_ERR_503, ex.getMessage());
        if(ex.getFailureType().equals(HystrixRuntimeException.FailureType.SHORTCIRCUIT)) {
            errorData.setMessage(ExceptionConstants.TOO_MANY_ERRORS);
            errorData.setCode(ExceptionConstants.ARD_API_ERR_100);
        } else if (ex.getFailureType().equals(HystrixRuntimeException.FailureType.TIMEOUT)) {
            errorData.setCode(ARD_API_ERR_503);
            errorData.setMessage(AccountRequestDataConstant.TIME_OUT_MSG);
        }
        exceptionLogger.exception(request.getHeader(X_LBG_TXN_CORRELATION_ID), ex.getCause());
        exceptionLogger.fatal(request.getHeader(X_LBG_TXN_CORRELATION_ID), ex.getCause().getMessage());
        return new ResponseEntity<>(errorData, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
}
