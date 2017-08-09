package com.lbg.aaf.entitlement.entitlementaccountrequestdata.service;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.AccountRequestStatusEnum;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.EntitlementOutputData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.EntitlementStatusUpdateInputData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.EntitlementUpdateFailedException;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.ResourceAccessException;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.handler.ErrorData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.AccountRequestDataConstant;
import com.lbg.ob.logger.Logger;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.concurrent.ExecutionException;

import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.ExceptionConstants.ARD_API_ERR_503;
import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.AccountRequestDataConstant.*;

@Service
@DefaultProperties(defaultFallback = "fallback")
public class EntitlementProxyServiceImpl implements EntitlementProxyService {

    public static final String ENTITLEMENT_NOT_REVOKED = "Entitlement couldnt be revoked";
    public static final String ENTITLEMENT_SERVICE_ERROR = "Error calling entitlement revoke operation";

    @Value("${dependentServices.entitlementRevokeUrl}")
    private String requestURL;

    @Autowired
    private Logger logger;

    private String correlationId;

    @Override
    @HystrixCommand(commandKey = "entitlementService", ignoreExceptions = {EntitlementUpdateFailedException.class})
    public void revokeEntitlement(Long entitlementId, String internalUserId, String internalUserRole, String correlationId) throws ExecutionException, InterruptedException {
        logger.logTrace(correlationId, "ENTRY -> revokeEntitlement");
        this.correlationId = correlationId;
        AsyncRestTemplate restTemplate = new AsyncRestTemplate();
        HttpHeaders requestHeaders = getHttpHeaders(internalUserId, internalUserRole, correlationId);
        EntitlementStatusUpdateInputData entitlementStatusUpdateInputData = new EntitlementStatusUpdateInputData(entitlementId);
        HttpEntity<EntitlementStatusUpdateInputData> httpEntity = new HttpEntity<>(entitlementStatusUpdateInputData, requestHeaders);
        ListenableFuture<ResponseEntity<EntitlementOutputData[]>> future = restTemplate.exchange(requestURL, HttpMethod.PUT, httpEntity, EntitlementOutputData[].class);
        //waits for the result
        ResponseEntity<EntitlementOutputData[]> entities = future.get();
        int value = entities.getStatusCode().value();
        if(value != 200) {
            logger.logInfo(correlationId, "Entitlement API returned status code as - "+ value);
            throw new EntitlementUpdateFailedException(ENTITLEMENT_NOT_REVOKED);
        }
        EntitlementOutputData[] body = entities.getBody();
        String updatedEntitlementStatus = body[0].getUpdatedEntitlementStatus();
        if(!(AccountRequestStatusEnum.REVOKED.getValue()).equalsIgnoreCase(updatedEntitlementStatus)) {
            logger.logInfo(correlationId, "Entitlement API returned Entitlement status as - "+ updatedEntitlementStatus);
            throw new EntitlementUpdateFailedException(ENTITLEMENT_NOT_REVOKED);
        }

        logger.logTrace(correlationId, "EXIT -> revokeEntitlement");
    }

    private HttpHeaders getHttpHeaders(String internalUserId, String internalUserRole, String correlationId) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.add(X_LBG_INTERNAL_USER_ID, internalUserId);
        requestHeaders.add(X_LBG_INTERNAL_USER_ROLE, internalUserRole);
        requestHeaders.add(X_LBG_TXN_CORRELATION_ID, correlationId);
        return requestHeaders;
    }

    public void fallback(Throwable ex) {                                                                                                                            //NOSONAR
        logger.logException(correlationId, ex);                                                                                                                     //NOSONAR
        ErrorData errorData = new ErrorData(Long.valueOf(HttpStatus.SERVICE_UNAVAILABLE.toString()), ARD_API_ERR_503, AccountRequestDataConstant.TIME_OUT_MSG);     //NOSONAR
        throw new ResourceAccessException(errorData, ex.getCause());                                                                                                //NOSONAR
    }
}
