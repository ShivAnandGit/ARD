package com.lbg.aaf.entitlement.entitlementaccountrequestdata.service;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.*;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.RecordNotFoundException;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository.AccountRequestInfoRepository;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository.AccountRequestStatusChangeHistoryRepository;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository.ProviderPermissionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.ExceptionConstants.ARD_API_ERR_005;
import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.ExceptionConstants.NOT_FOUND;

/**
 * Created by pbabb1 on 7/19/17.
 */
@Service
public class AccountRequestDAOImpl implements AccountRequestDAO {

    @Autowired
    private AccountRequestInfoRepository accountRequestInfoRepository;

    @Autowired
    private ProviderPermissionsRepository providerPermissionsRepository;

    @Autowired
    private AccountRequestStatusChangeHistoryRepository accountRequestInfoHistoryRepository;

    @Transactional(rollbackOn = {InterruptedException.class})
    @Override
    public AccountRequestOutputResponse createAccountRequest(AccountRequest accountRequestInfo) throws IOException, InterruptedException {
        AccountRequestOutputResponse accountRequestOutputResponse = null;
        AccountRequest savedAccountRequestInfo = accountRequestInfoRepository.save(accountRequestInfo);
        saveAccountRequestStatusHistory(savedAccountRequestInfo, InternalUserRoleEnum.CUSTOMER);
        accountRequestOutputResponse = new AccountRequestOutputResponse(savedAccountRequestInfo.getAccountRequestExternalIdentifier(), savedAccountRequestInfo.getAccountRequestStatus(), savedAccountRequestInfo.getCreatedDateTime(), savedAccountRequestInfo.getAccountRequestJsonString());
        List<ProviderPermission> refPermissions = getRefPermissionsWithMetadata(accountRequestOutputResponse.getAccountRequestOutputData().getPermissions());
        accountRequestOutputResponse.getAccountRequestOutputData().setPermissions(refPermissions);
        return accountRequestOutputResponse;
    }

    @Override
    public AccountRequestOutputResponse findAccountRequest(String accountRequestId, String clientId) throws IOException {
        String status = AccountRequestStatusEnum.AWAITINGAUTHORISATION.getValue();
        AccountRequest savedAccountRequestInfo = accountRequestInfoRepository.findByAccountRequestExternalIdentifierAndProviderClientIdAndAccountRequestStatus(accountRequestId, clientId, status);
        if(savedAccountRequestInfo == null) {
            throw new RecordNotFoundException(NOT_FOUND, ARD_API_ERR_005);
        }
        AccountRequestOutputResponse accountRequestOutputResponse = new AccountRequestOutputResponse(savedAccountRequestInfo.getAccountRequestExternalIdentifier(), savedAccountRequestInfo.getAccountRequestStatus(), savedAccountRequestInfo.getCreatedDateTime(), savedAccountRequestInfo.getAccountRequestJsonString());
        List<ProviderPermission> refPermissions = getRefPermissionsWithMetadata(accountRequestOutputResponse.getAccountRequestOutputData().getPermissions());
        accountRequestOutputResponse.getAccountRequestOutputData().setPermissions(refPermissions);
        return accountRequestOutputResponse;
    }

    @Override
    public AccountRequestOutputResponse findAccountRequest(String accountRequestId) throws IOException {
        AccountRequest savedAccountRequestInfo = getAccountRequest(accountRequestId);
        AccountRequestOutputResponse accountRequestOutputResponse = new AccountRequestOutputResponse(savedAccountRequestInfo.getAccountRequestExternalIdentifier(), savedAccountRequestInfo.getAccountRequestStatus(), savedAccountRequestInfo.getCreatedDateTime(), savedAccountRequestInfo.getAccountRequestJsonString());
        List<ProviderPermission> refPermissions = getRefPermissionsWithMetadata(accountRequestOutputResponse.getAccountRequestOutputData().getPermissions());
        accountRequestOutputResponse.getAccountRequestOutputData().setPermissions(refPermissions);
        return accountRequestOutputResponse;
    }



    @Transactional(rollbackOn = {InterruptedException.class})
    @Override
    public AccountRequestStatusHistory updateAccountRequest(AccountRequest accountRequestInfo, InternalUserRoleEnum role) {
        AccountRequest savedAccountRequestInfo = accountRequestInfoRepository.save(accountRequestInfo);
        return saveAccountRequestStatusHistory(savedAccountRequestInfo, role);
    }

    @Transactional(rollbackOn = {InterruptedException.class})
    @Override
    public void revokeAccountRequest(String clientRole, AccountRequest accountRequestInfo, AccountRequestStatusEnum updateableStatus) {
        //update the status in ACCT_REQUEST
        accountRequestInfo.setAccountRequestStatus(updateableStatus);
        AccountRequest savedAccountRequestInfo = accountRequestInfoRepository.save(accountRequestInfo);

        //update the history
        InternalUserRoleEnum role = InternalUserRoleEnum.valueOf(clientRole.toUpperCase());
        saveAccountRequestStatusHistory(savedAccountRequestInfo, role);
    }

    @Override
    public AccountRequest getAccountRequest(String accountRequestId) {
        AccountRequest accountRequestInfo = accountRequestInfoRepository.findByAccountRequestExternalIdentifier(accountRequestId);
        if(accountRequestInfo == null) {
            throw new RecordNotFoundException(NOT_FOUND, ARD_API_ERR_005);
        }
        return accountRequestInfo;
    }


    private AccountRequestStatusHistory saveAccountRequestStatusHistory(AccountRequest savedAccountRequestInfo, InternalUserRoleEnum role) {
        AccountRequestStatusHistory accountRequestStatusChangeHistory = new AccountRequestStatusHistory();
        accountRequestStatusChangeHistory.setAccountRequestInfoId(savedAccountRequestInfo.getAccountRequestIdentifier());
        accountRequestStatusChangeHistory.setResourceStatus(savedAccountRequestInfo.getAccountRequestStatus());
        accountRequestStatusChangeHistory.setStatusUpdatedByRole(role);
        AccountRequestStatusHistory save = accountRequestInfoHistoryRepository.save(accountRequestStatusChangeHistory);
        return save;
    }

    private List<ProviderPermission> getRefPermissionsWithMetadata(List<ProviderPermission> permissions) {
        List<ProviderPermission> refPermissions = new ArrayList<>();
        permissions.forEach(permission -> {
            refPermissions.addAll(providerPermissionsRepository.findByCode(permission.getCode()));
        });
        return refPermissions;
    }
}
