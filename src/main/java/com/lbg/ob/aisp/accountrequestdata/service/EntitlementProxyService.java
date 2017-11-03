package com.lbg.ob.aisp.accountrequestdata.service;

import java.util.concurrent.ExecutionException;

public interface EntitlementProxyService {
    public void revokeEntitlement(Long entitlementId, String internalUserId, String internalUserRole, String correlationId, String clientId, Boolean fovIndicator) throws ExecutionException, InterruptedException;
}
