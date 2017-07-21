package com.lbg.aaf.entitlement.entitlementaccountrequestdata.service;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.*;

import java.io.IOException;

/**
 * Created by pbabb1 on 7/19/17.
 */
public interface AccountRequestDAO {

    AccountRequestOutputResponse createAccountRequest(AccountRequest accountRequestInfo) throws IOException, InterruptedException;

    AccountRequestOutputResponse findAccountRequest(String accountRequestId, String clientId) throws IOException;

    AccountRequestOutputResponse findAccountRequest(String accountRequestId) throws IOException;

    AccountRequestStatusHistory updateAccountRequest(AccountRequest accountRequestInfo, InternalUserRoleEnum role);

    void revokeAccountRequest(String clientRole, AccountRequest accountRequestInfo, AccountRequestStatusEnum updateableStatus);

    AccountRequest getAccountRequest(String accountRequestId);
}
