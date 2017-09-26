package com.lbg.ob.aisp.accountrequestdata.service;

import com.lbg.ob.aisp.accountrequestdata.data.AccountRequest;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestOutputResponse;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestStatusEnum;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestStatusHistory;
import com.lbg.ob.aisp.accountrequestdata.data.InternalUserRoleEnum;

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
