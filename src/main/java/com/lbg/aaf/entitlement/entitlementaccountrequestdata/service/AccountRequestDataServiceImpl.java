package com.lbg.aaf.entitlement.entitlementaccountrequestdata.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.*;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.InvalidRequestException;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.RecordNotFoundException;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository.AccountRequestInfoRepository;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository.AccountRequestStatusChangeHistoryRepository;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository.ProviderPermissionsRepository;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.AccountRequestDataConstant;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.StateChangeMachine;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.ExceptionConstants.*;

/**
 * Class AccountRequestDataServiceImpl.All Service Class will implement this interface.
 * @author Amit Jain
 */

@Service
public class AccountRequestDataServiceImpl<T> implements AccountRequestDataService<T> {

    @Autowired
    private AccountRequestInfoRepository accountRequestInfoRepository;

    @Autowired
    private AccountRequestStatusChangeHistoryRepository accountRequestInfoHistoryRepository;

    @Autowired
    private ProviderPermissionsRepository providerPermissionsRepository;

    @Autowired
    private EntitlementService entitlementService;

    @Autowired
    private StateChangeMachine stateChangeMachine;
    /**
     * createAccountRequestData takes CreateAccountInputData as input and will create entitlement
     * for each request .
     * @param CreateAccountInputData createAccountInputRequest
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException
     * @throws URISyntaxException
     */
    @Transactional
    public AccountRequestOutputResponse createAccountRequestData(CreateAccountInputRequest createAccountInputRequest, String clientId, String fapiFinancialId)
            throws IOException, URISyntaxException {
        String json = getJsonRequest(createAccountInputRequest);
        AccountRequest accountRequestInfo = new AccountRequest(createAccountInputRequest.getCreateAccountInputData(), clientId, fapiFinancialId, json);
        AccountRequest savedAccountRequestInfo = accountRequestInfoRepository.save(accountRequestInfo);
        saveAccountRequestStatusHistory(savedAccountRequestInfo, InternalUserRoleEnum.CUSTOMER);

        AccountRequestOutputResponse accountRequestOutputResponse = new AccountRequestOutputResponse(savedAccountRequestInfo.getAccountRequestExternalIdentifier(), savedAccountRequestInfo.getAccountRequestStatus(), savedAccountRequestInfo.getCreatedDateTime(), savedAccountRequestInfo.getAccountRequestJsonString());
        List<ProviderPermission> refPermissions = getRefPermissionsWithMetadata(accountRequestOutputResponse.getAccountRequestOutputData().getPermissions());
        accountRequestOutputResponse.getAccountRequestOutputData().setPermissions(refPermissions);
        return accountRequestOutputResponse;
    }

    private String getJsonRequest(CreateAccountInputRequest request) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(request);
    }

    private List<ProviderPermission> getRefPermissionsWithMetadata(List<ProviderPermission> permissions) {
        List<ProviderPermission> refPermissions = new ArrayList<>();
        permissions.forEach(permission -> {
            refPermissions.addAll(providerPermissionsRepository.findByCode(permission.getCode()));
        });
        return refPermissions;
    }

    /**
     * findByAccountRequestExternalIdentifierAndProviderClientId takes accountRequestId and clientId as input and will return the account request data associated with the request and client id
     * @param String accountRequestId
     * @param String clientId
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException
     */
    public AccountRequestOutputResponse findByAccountRequestExternalIdentifierAndProviderClientId(String accountRequestId, String clientId) throws IOException {
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

    /**
     * findByAccountRequestExternalIdentifier takes accountRequestId as input and will return the account request data associated with the request and client id
     * @param String accountRequestId
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException
     */
    public AccountRequestOutputResponse findByAccountRequestExternalIdentifier(String accountRequestId) throws IOException {
        AccountRequest savedAccountRequestInfo = getAccountRequest(accountRequestId);
        AccountRequestOutputResponse accountRequestOutputResponse = new AccountRequestOutputResponse(savedAccountRequestInfo.getAccountRequestExternalIdentifier(), savedAccountRequestInfo.getAccountRequestStatus(), savedAccountRequestInfo.getCreatedDateTime(), savedAccountRequestInfo.getAccountRequestJsonString());
        List<ProviderPermission> refPermissions = getRefPermissionsWithMetadata(accountRequestOutputResponse.getAccountRequestOutputData().getPermissions());
        accountRequestOutputResponse.getAccountRequestOutputData().setPermissions(refPermissions);
        return accountRequestOutputResponse;
    }



    /**
     * updateAccountRequestData UpdateAccountInputdata as input and will update the associated account request data associated with the request and client id
     * @param UpdateAccountInputData accountInputData
     * @param String accountRequestId
     * @param String clientId
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException, URISyntaxException
     */
    @Override
    @Transactional
    public UpdateAccountRequestOutputData updateAccountRequestData(UpdateAccountRequestInputData accountInputData, String accountRequestId, String clientRole) throws IOException, URISyntaxException {
        AccountRequest accountRequestInfo = getAccountRequest(accountRequestId);
        String possibleStatus = accountInputData.getStatus();
        if(!(AccountRequestDataConstant.AUTHORISED.equalsIgnoreCase(possibleStatus) || AccountRequestDataConstant.REJECTED.equalsIgnoreCase(possibleStatus))) {
            throw new InvalidRequestException(BAD_REQUEST_INVALID_REQUEST, ARD_API_ERR_007);
        }
        AccountRequestStatusEnum accountRequestStatusEnum = stateChangeMachine.getUpdatableStatus(accountRequestInfo.getAccountRequestStatus(), possibleStatus);
        //update the status in ACCT_REQUEST
        accountRequestInfo.setAccountRequestStatus(accountRequestStatusEnum);
        if(accountRequestStatusEnum.equals(AccountRequestStatusEnum.AUTHORISED)) {
            Long entitlementId = accountInputData.getEntitlementId();
            if(entitlementId == null || entitlementId == 0) {
                throw new InvalidRequestException("Request can't be authorised without an Entitlement Id", "ARD_API_ERR_999");
            }
            accountRequestInfo.setEntitlementId(entitlementId);
        }
        AccountRequest savedAccountRequestInfo = accountRequestInfoRepository.save(accountRequestInfo);

        //update the history
        InternalUserRoleEnum role = InternalUserRoleEnum.valueOf(clientRole.toUpperCase());
        AccountRequestStatusHistory savedAccountRequestStatusHistory = saveAccountRequestStatusHistory(savedAccountRequestInfo, role);

        UpdateAccountRequestOutputData updateAccountRequestOutputData = new UpdateAccountRequestOutputData();
        updateAccountRequestOutputData.setAccountRequestId(savedAccountRequestInfo.getAccountRequestExternalIdentifier());
        updateAccountRequestOutputData.setUpdatedAtTimestamp(Util.formatDateAsISO8601(savedAccountRequestStatusHistory.getStatusUpdatedDateTime().getTime()));
        updateAccountRequestOutputData.setUpdatedStatus(accountRequestStatusEnum.getValue());
        return updateAccountRequestOutputData;
    }

    private AccountRequest getAccountRequest(String accountRequestId) {
        AccountRequest accountRequestInfo = accountRequestInfoRepository.findByAccountRequestExternalIdentifier(accountRequestId);
        if(accountRequestInfo == null) {
            throw new RecordNotFoundException(NOT_FOUND, ARD_API_ERR_005);
        }
        return accountRequestInfo;
    }

    @Override
    @Transactional
    public void revokeAccountRequestData(String accountRequestId, String clientRole, String txnCorrelationId) throws IOException, URISyntaxException {
        AccountRequest accountRequestInfo = getAccountRequest(accountRequestId);

        String accountRequestStatus = accountRequestInfo.getAccountRequestStatus();
        AccountRequestStatusEnum updateableStatus = stateChangeMachine.getUpdatableStatus(accountRequestStatus, AccountRequestStatusEnum.REVOKED);
        //call the entitlement API to revoke the entitlement, if the status was authorised
        if(accountRequestStatus.equals(AccountRequestDataConstant.AUTHORISED)) {
            Long entitlementId = accountRequestInfo.getEntitlementId();
            entitlementService.revokeEntitlement(entitlementId, InternalUserRoleEnum.SYSTEM.toString(), clientRole, txnCorrelationId);
        }
        //update the status in ACCT_REQUEST
        accountRequestInfo.setAccountRequestStatus(updateableStatus);
        AccountRequest savedAccountRequestInfo = accountRequestInfoRepository.save(accountRequestInfo);

        //update the history
        InternalUserRoleEnum role = InternalUserRoleEnum.valueOf(clientRole.toUpperCase());
        saveAccountRequestStatusHistory(savedAccountRequestInfo, role);
    }



    private AccountRequestStatusHistory saveAccountRequestStatusHistory(AccountRequest savedAccountRequestInfo, InternalUserRoleEnum role) {
        AccountRequestStatusHistory accountRequestStatusChangeHistory = new AccountRequestStatusHistory();
        accountRequestStatusChangeHistory.setAccountRequestInfoId(savedAccountRequestInfo.getAccountRequestIdentifier());
        accountRequestStatusChangeHistory.setResourceStatus(savedAccountRequestInfo.getAccountRequestStatus());
        accountRequestStatusChangeHistory.setStatusUpdatedByRole(role);
        return accountRequestInfoHistoryRepository.save(accountRequestStatusChangeHistory);
    }

}
