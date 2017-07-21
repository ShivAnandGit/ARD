package com.lbg.aaf.entitlement.entitlementaccountrequestdata.service;

public interface EntitlementProxyService {
    public void revokeEntitlement(Long entitlementId, String internalUserId, String internalUserRole, String correlationId);
}
