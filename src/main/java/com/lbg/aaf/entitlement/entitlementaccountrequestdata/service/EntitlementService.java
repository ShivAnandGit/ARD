package com.lbg.aaf.entitlement.entitlementaccountrequestdata.service;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.EntitlementUpdateFailedException;

public interface EntitlementService {
    public void revokeEntitlement(Long entitlementId, String internalUserId, String internalUserRole, String correlationId);
}
