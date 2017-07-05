package com.lbg.aaf.entitlement.entitlementaccountrequestdata.service;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.AccountRequestStatusEnum;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.EntitlementOutputData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.EntitlementStatusUpdateInputData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.EntitlementUpdateFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.AccountRequestDataConstant.X_LBG_INTERNAL_USER_ID;
import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.AccountRequestDataConstant.X_LBG_INTERNAL_USER_ROLE;
import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.AccountRequestDataConstant.X_LBG_TXN_CORRELATION_ID;

@Service
@PropertySource("classpath:app.properties")
public class EntitlementServiceImpl implements EntitlementService {

    @Value("${entitlementURL}")
    private String requestURL;
    
    @Override
    public void revokeEntitlement(Long entitlementId, String internalUserId, String internalUserRole, String correlationId) {
        AsyncRestTemplate restTemplate = new AsyncRestTemplate();
        HttpHeaders requestHeaders = getHttpHeaders(internalUserId, internalUserRole, correlationId);
        EntitlementStatusUpdateInputData entitlementStatusUpdateInputData = new EntitlementStatusUpdateInputData(entitlementId);
        HttpEntity<EntitlementStatusUpdateInputData> httpEntity = new HttpEntity<>(entitlementStatusUpdateInputData, requestHeaders);
        ListenableFuture<ResponseEntity<EntitlementOutputData[]>> future = restTemplate.exchange(requestURL, HttpMethod.PUT, httpEntity, EntitlementOutputData[].class);
        try {
            //waits for the result
            ResponseEntity<EntitlementOutputData[]> entities = future.get();
            if(entities.getStatusCode().value() != 200) {
                throw new EntitlementUpdateFailedException("Entitlement couldnt be revoked");
            }
            EntitlementOutputData[] body = entities.getBody();
            if(!(AccountRequestStatusEnum.REVOKED.getValue()).equalsIgnoreCase(body[0].getUpdatedEntitlementStatus())) {
                throw new EntitlementUpdateFailedException("Entitlement couldnt be revoked");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new EntitlementUpdateFailedException("Error calling entitlement revoke operation", e);
        }
    }

    private HttpHeaders getHttpHeaders(String internalUserId, String internalUserRole, String correlationId) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        requestHeaders.add(X_LBG_INTERNAL_USER_ID, internalUserId);
        requestHeaders.add(X_LBG_INTERNAL_USER_ROLE, internalUserRole);
        requestHeaders.add(X_LBG_TXN_CORRELATION_ID, correlationId);
        return requestHeaders;
    }
}
