package com.lbg.ob.aisp.accountrequestdata.service;

import com.lbg.ob.aisp.accountrequestdata.data.AccountRequest;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestOutputResponse;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestStatusEnum;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestStatusHistory;
import com.lbg.ob.aisp.accountrequestdata.data.InternalUserRoleEnum;
import com.lbg.ob.aisp.accountrequestdata.data.ProviderPermission;
import com.lbg.ob.aisp.accountrequestdata.exception.ExceptionConstants;
import com.lbg.ob.aisp.accountrequestdata.exception.RecordNotFoundException;
import com.lbg.ob.aisp.accountrequestdata.repository.AccountRequestInfoRepository;
import com.lbg.ob.aisp.accountrequestdata.repository.AccountRequestStatusChangeHistoryRepository;
import com.lbg.ob.aisp.accountrequestdata.repository.ProviderPermissionsRepository;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private static final Logger logger = LogManager.getLogger(EntitlementProxyServiceImpl.class);

    @Transactional(rollbackOn = {InterruptedException.class})
    @Override
    public AccountRequestOutputResponse createAccountRequest(AccountRequest accountRequestInfo) throws IOException, InterruptedException {
        AccountRequestOutputResponse accountRequestOutputResponse = null;
        AccountRequest savedAccountRequestInfo = accountRequestInfoRepository.save(accountRequestInfo);
        saveAccountRequestStatusHistory(savedAccountRequestInfo, InternalUserRoleEnum.CUSTOMER);
        accountRequestOutputResponse = new AccountRequestOutputResponse(savedAccountRequestInfo.getAccountRequestExternalIdentifier(), savedAccountRequestInfo.getAccountRequestStatus(), savedAccountRequestInfo.getEntitlementAccessCode(), savedAccountRequestInfo.getCreatedDateTime(), savedAccountRequestInfo.getAccountRequestJsonString());
        List<ProviderPermission> refPermissions = getRefPermissionsWithMetadata(accountRequestOutputResponse.getAccountRequestOutputData().getPermissions());
        accountRequestOutputResponse.getAccountRequestOutputData().setPermissions(refPermissions);
        return accountRequestOutputResponse;
    }

    @Override
    public AccountRequestOutputResponse findAccountRequest(String accountRequestId, String clientId) throws IOException {
        AccountRequest savedAccountRequestInfo = accountRequestInfoRepository.findByAccountRequestExternalIdentifierAndProviderClientIdAndAccountRequestStatus(accountRequestId, clientId);
        if (savedAccountRequestInfo == null) {
            throw new RecordNotFoundException(ExceptionConstants.NOT_FOUND, ExceptionConstants.ARD_API_ERR_005);
        }
        AccountRequestOutputResponse accountRequestOutputResponse = new AccountRequestOutputResponse(savedAccountRequestInfo.getAccountRequestExternalIdentifier(), 
        		                                                    savedAccountRequestInfo.getAccountRequestStatus(),
        		                                                    savedAccountRequestInfo.getEntitlementAccessCode(),
        		                                                    savedAccountRequestInfo.getCreatedDateTime(), savedAccountRequestInfo.getAccountRequestJsonString());
        List<ProviderPermission> refPermissions = getRefPermissionsWithMetadata(accountRequestOutputResponse.getAccountRequestOutputData().getPermissions());
        accountRequestOutputResponse.getAccountRequestOutputData().setPermissions(refPermissions);
        return accountRequestOutputResponse;
    }

    @Override
    public AccountRequestOutputResponse findAccountRequest(String accountRequestId) throws IOException {
        AccountRequest savedAccountRequestInfo = getAccountRequest(accountRequestId);
        AccountRequestOutputResponse accountRequestOutputResponse = new AccountRequestOutputResponse(savedAccountRequestInfo.getAccountRequestExternalIdentifier(), savedAccountRequestInfo.getAccountRequestStatus(), savedAccountRequestInfo.getEntitlementAccessCode(), savedAccountRequestInfo.getCreatedDateTime(), savedAccountRequestInfo.getAccountRequestJsonString());
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
        if (accountRequestInfo == null) {
            throw new RecordNotFoundException(ExceptionConstants.NOT_FOUND, ExceptionConstants.ARD_API_ERR_005);
        }
        return accountRequestInfo;
    }

    private AccountRequestStatusHistory saveAccountRequestStatusHistory(AccountRequest savedAccountRequestInfo, InternalUserRoleEnum role) {
        logger.debug("Saving the accountRequestInfoHistory");
        AccountRequestStatusHistory accountRequestStatusChangeHistory = new AccountRequestStatusHistory();
        accountRequestStatusChangeHistory.setAccountRequestInfoId(savedAccountRequestInfo.getAccountRequestIdentifier());
        accountRequestStatusChangeHistory.setResourceStatus(savedAccountRequestInfo.getAccountRequestStatus());
        accountRequestStatusChangeHistory.setStatusUpdatedByRole(role);
        AccountRequestStatusHistory save = accountRequestInfoHistoryRepository.save(accountRequestStatusChangeHistory);
        return save;
    }

    private List<ProviderPermission> getRefPermissionsWithMetadata(List<ProviderPermission> permissions) {
        logger.debug("Getting the ref permissions with metadata");
        List<ProviderPermission> refPermissions = new ArrayList<>();
        permissions.forEach(permission -> {
            refPermissions.addAll(providerPermissionsRepository.findByCode(permission.getCode()));
        });
        return refPermissions;
    }
}
