package com.lbg.ob.aisp.accountrequestdata.service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpHeaders;

public interface EntitlementProxyService {
    public void revokeEntitlement(Long entitlementId, String internalUserId, String internalUserRole, String clientId, Boolean fovIndicator, Map<String, String> headers) throws ExecutionException, InterruptedException;

}
