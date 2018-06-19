package com.lbg.ob.aisp.accountrequestdata.service;

import static com.lbg.ob.aisp.accountrequestdata.exception.ExceptionConstants.ARD_API_ERR_503;
import static com.lbg.ob.aisp.accountrequestdata.util.AccountRequestDataConstant.X_LBG_FOV_INDICATOR;
import static com.lbg.ob.aisp.accountrequestdata.util.AccountRequestDataConstant.X_LBG_INTERNAL_USER_ID;
import static com.lbg.ob.aisp.accountrequestdata.util.AccountRequestDataConstant.X_LBG_INTERNAL_USER_ROLE;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.ResponseErrorHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestStatusEnum;
import com.lbg.ob.aisp.accountrequestdata.data.EntitlementOutputData;
import com.lbg.ob.aisp.accountrequestdata.data.EntitlementStatusUpdateInputData;
import com.lbg.ob.aisp.accountrequestdata.exception.EntitlementUpdateFailedException;
import com.lbg.ob.aisp.accountrequestdata.exception.ResourceAccessException;
import com.lbg.ob.aisp.accountrequestdata.exception.handler.ErrorData;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
@DefaultProperties(defaultFallback = "fallback")
public class EntitlementProxyServiceImpl implements EntitlementProxyService {

    private static final Logger logger = LogManager.getLogger(EntitlementProxyServiceImpl.class);

    public static final String ENTITLEMENT_NOT_REVOKED = "Entitlement couldnt be revoked";
    public static final String X_LBG_UPDATED_BY_USER_ID = "x-lbg-updated-by-user-id";
    public static final String X_LBG_TXN_CORRELATION_ID = "x-lbg-txn-correlation-id";

    @Value("${entitlementRevokeUrl}")
    private String requestURL;

    @Override
    @HystrixCommand(commandKey = "entitlementService", ignoreExceptions = {EntitlementUpdateFailedException.class})
    public void revokeEntitlement(Long entitlementId, String internalUserId, String internalUserRole, String clientId, Boolean fovIndicator, Map<String,String> headers)
            throws ExecutionException, InterruptedException {
        logger.trace("ENTRY --> revokeEntitlement");
        AsyncRestTemplate restTemplate = new AsyncRestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return (!HttpStatus.OK.equals(response.getStatusCode()));
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                try {
                    InputStream body = response.getBody();
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(body, writer, StandardCharsets.UTF_8);
                    ObjectMapper objectMapper = new ObjectMapper();
                    String content = writer.toString();
                    TypeReference<Map<String, ErrorData>> typeRef = new TypeReference<Map<String, ErrorData>>() {
                    };
                    Map<String, ErrorData> map = objectMapper.readValue(content, typeRef);
                    ErrorData errorData = map.get("error");
                    throw new EntitlementUpdateFailedException(errorData);
                } catch (IOException ioe) {
                    logger.error("Error while calling Entitlement Service", ioe);
                    throw new EntitlementUpdateFailedException(new ErrorData(Long.valueOf(response.getRawStatusCode()), null, ENTITLEMENT_NOT_REVOKED));
                }
            }
        });
        HttpHeaders requestHeaders = getHttpHeaders(internalUserId, internalUserRole, clientId, fovIndicator, headers);
        EntitlementStatusUpdateInputData entitlementStatusUpdateInputData = new EntitlementStatusUpdateInputData(entitlementId);
        HttpEntity<EntitlementStatusUpdateInputData> httpEntity = new HttpEntity<>(entitlementStatusUpdateInputData, requestHeaders);
        ListenableFuture<ResponseEntity<EntitlementOutputData[]>> future = restTemplate.exchange(requestURL, HttpMethod.PUT, httpEntity, EntitlementOutputData[].class);
        //waits for the result
        ResponseEntity<EntitlementOutputData[]> entities = future.get();
        EntitlementOutputData[] body = entities.getBody();
        String updatedEntitlementStatus = body[0].getUpdatedEntitlementStatus();
        if (!(AccountRequestStatusEnum.REVOKED.getValue()).equalsIgnoreCase(updatedEntitlementStatus)) {
            logger.info("Entitlement API returned Entitlement status as - " + updatedEntitlementStatus);
            throw new EntitlementUpdateFailedException(ENTITLEMENT_NOT_REVOKED);
        }
        logger.trace("revokeEntitlement <-- EXIT");
    }

    private HttpHeaders getHttpHeaders(String internalUserId, String internalUserRole, String clientId, Boolean fovIndicator, Map<String,String> headers) {
        logger.debug("creating http headers");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAll(headers);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.set(X_LBG_INTERNAL_USER_ID, internalUserId);
        requestHeaders.set(X_LBG_INTERNAL_USER_ROLE, internalUserRole);
        requestHeaders.set(X_LBG_UPDATED_BY_USER_ID, clientId);
        requestHeaders.set(X_LBG_TXN_CORRELATION_ID, "String");
        String fov = (fovIndicator == null) ? null : fovIndicator.toString();
        requestHeaders.set(X_LBG_FOV_INDICATOR, fov);
        return requestHeaders;
    }

    public void fallback(Throwable ex) { //NOSONAR
        if (ex instanceof ExecutionException) {
            Throwable cause = ex.getCause();
            if (cause instanceof EntitlementUpdateFailedException) {
                EntitlementUpdateFailedException exception = (EntitlementUpdateFailedException) cause;
                throw exception;
            }
        }
        logger.error(ex); //NOSONAR
        ErrorData errorData = new ErrorData(Long.valueOf(HttpStatus.SERVICE_UNAVAILABLE.toString()), ARD_API_ERR_503, ex.getMessage()); //NOSONAR
        throw new ResourceAccessException(errorData, ex); //NOSONAR
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }
}
