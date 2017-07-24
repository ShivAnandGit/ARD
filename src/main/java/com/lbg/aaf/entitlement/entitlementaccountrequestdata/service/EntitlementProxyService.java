package com.lbg.aaf.entitlement.entitlementaccountrequestdata.service;

import java.util.concurrent.ExecutionException;

public interface EntitlementProxyService {
    public void revokeEntitlement(Long entitlementId, String internalUserId, String internalUserRole, String correlationId) throws ExecutionException, InterruptedException;
}
