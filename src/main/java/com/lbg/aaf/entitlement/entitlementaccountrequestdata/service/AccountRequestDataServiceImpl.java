package com.lbg.aaf.entitlement.entitlementaccountrequestdata.service;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.*;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository.AccountRequestAssociatedAccountRepository;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository.AccountRequestInfoRepository;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository.AccountRequestStatusChangeHistoryRepository;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class AccountRequestDataServiceImpl.All Service Class will implement this interface.
 * @author Amit Jain
 */

@Service
public class AccountRequestDataServiceImpl<T> implements AccountRequestDataService<T> {

    @Autowired
    AccountRequestInfoRepository accountRequestInfoRepository;

    @Autowired
    AccountRequestStatusChangeHistoryRepository accountRequestInfoHistoryRepository;

    @Autowired
    AccountRequestAssociatedAccountRepository associatedAccountsResourceRepository;

    /**
     * createAccountRequestData takes CreateAccountInputData as input and will create entitlement
     * for each request .
     * @param CreateAccountInputData createAccountInputData
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException
     * @throws URISyntaxException
     */
    @Transactional
    public AccountRequestOutputData createAccountRequestData(CreateAccountInputData createAccountInputData, String clientId)
            throws IOException, URISyntaxException {
        AccountRequest accountRequestInfo = new AccountRequest(createAccountInputData, clientId);
        AccountRequest savedAccountRequestInfo = accountRequestInfoRepository.save(accountRequestInfo);
        List<String> accountIds = new ArrayList<String>();
        for (AccountResource selectedAccount : createAccountInputData.getSelectedAccounts()) {
            saveAssociatedAccountsResource(savedAccountRequestInfo.getAccountRequestIdentifier(), selectedAccount.getAccount().getIdentification());
            accountIds.add(selectedAccount.getAccount().getIdentification());
        }

        saveAccountRequestStatusHistory(savedAccountRequestInfo, savedAccountRequestInfo.getProviderClientId(), InternalUserRoleEnum.CUSTOMER);


        AccountRequestOutputData accountRequestOutputData = new AccountRequestOutputData(savedAccountRequestInfo.getAccountRequestJsonString(), accountIds);
        return accountRequestOutputData;

    }

    /**
     * findByAccountRequestExternalIdentifierAndProviderClientId takes accountRequestId and clientId as input and will return the account request data associated with the request and client id
     * @param String accountRequestId
     * @param String clientId
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException
     */
    public AccountRequestOutputData findByAccountRequestExternalIdentifierAndProviderClientId(String accountRequestId, String clientId) throws IOException {
        String accountRequestInfoJSONString = accountRequestInfoRepository.findByAccountRequestExternalIdentifierAndProviderClientId(accountRequestId, clientId).getAccountRequestJsonString();
        List<AccountRequestAssociatedAccountResource> associatedAccountsResourceList = associatedAccountsResourceRepository.findByAccountRequestInfoId(accountRequestInfoRepository.findByAccountRequestExternalIdentifierAndProviderClientId(accountRequestId, clientId).getAccountRequestIdentifier());
        List<String> accountIds = new ArrayList<String>(associatedAccountsResourceList.size());
        for (AccountRequestAssociatedAccountResource associatedAccountsResource : associatedAccountsResourceList) {
            accountIds.add(associatedAccountsResource.getAccountIdentifier());
        }
        AccountRequestOutputData accountRequestOutputData = new AccountRequestOutputData(accountRequestInfoJSONString, accountIds);
        return accountRequestOutputData;
    }

    /**
     * findByAccountRequestExternalIdentifier takes accountRequestId as input and will return the account request data associated with the request and client id
     * @param String accountRequestId
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException
     */
    public AccountRequestOutputData findByAccountRequestExternalIdentifier(String accountRequestId) throws IOException {
        String accountRequestInfoJSONString = accountRequestInfoRepository.findByAccountRequestExternalIdentifier(accountRequestId).getAccountRequestJsonString();
        List<AccountRequestAssociatedAccountResource> associatedAccountsResourceList = associatedAccountsResourceRepository.findByAccountRequestInfoId(accountRequestInfoRepository.findByAccountRequestExternalIdentifier(accountRequestId).getAccountRequestIdentifier());
        List<String> accountIds = new ArrayList<String>(associatedAccountsResourceList.size());
        for (AccountRequestAssociatedAccountResource associatedAccountsResource : associatedAccountsResourceList) {
            accountIds.add(associatedAccountsResource.getAccountIdentifier());
        }
        AccountRequestOutputData accountRequestOutputData = new AccountRequestOutputData(accountRequestInfoJSONString, accountIds);
        return accountRequestOutputData;

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
    public UpdateRequestOutputData updateAccountRequestData(UpdateAccountInputData accountInputData, String accountRequestId, String clientId, String clientRole) throws IOException, URISyntaxException {
        AccountRequestStatusEnum accountRequestStatusEnum = getAccountRequestStatusEnum(accountInputData);
        AccountRequest accountRequestInfo = accountRequestInfoRepository.findByAccountRequestExternalIdentifier(accountRequestId);

        //save the data in ACCT_REQ_ASSOCIATED_ACCT
        for (String selectedAccount : accountInputData.getAccountIds()) {
            saveAssociatedAccountsResource(accountRequestInfo.getAccountRequestIdentifier(), selectedAccount);
        }

        //update the status in ACCT_REQUEST
        accountRequestInfo.setAccountRequestStatus(accountRequestStatusEnum);
        AccountRequest savedAccountRequestInfo = accountRequestInfoRepository.save(accountRequestInfo);

        //update the history
        InternalUserRoleEnum role = InternalUserRoleEnum.valueOf(clientRole.toUpperCase());
        AccountRequestStatusHistory savedAccountRequestStatusHistory = saveAccountRequestStatusHistory(savedAccountRequestInfo, clientId, role);

        UpdateRequestOutputData updateRequestOutputData = new UpdateRequestOutputData();
        updateRequestOutputData.setAccountRequestId(savedAccountRequestInfo.getAccountRequestExternalIdentifier());
        updateRequestOutputData.setUpdatedAtTimestamp(Util.formatDateAsISO8601(savedAccountRequestStatusHistory.getStatusUpdatedDateTime().getTime()));
        updateRequestOutputData.setUpdatedStatus(accountRequestStatusEnum.getValue());
        return updateRequestOutputData;
    }

    private AccountRequestStatusEnum getAccountRequestStatusEnum(UpdateAccountInputData accountInputData) {
        AccountRequestStatusEnum accountRequestStatusEnum;
        try {
            accountRequestStatusEnum = AccountRequestStatusEnum.valueOf(accountInputData.getStatus().toUpperCase());
        } catch(IllegalArgumentException ex) {
            //TODO: Temporary code to throw an exception as of now, would be replaced by Error handling
            throw new RuntimeException("The request isn't to authorise, not proceeding further.", ex);
        }
        if(!(accountRequestStatusEnum.equals(AccountRequestStatusEnum.AUTHORISED) || accountRequestStatusEnum.equals(AccountRequestStatusEnum.REJECTED)) ) {
            //TODO: Temporary code to throw an exception as of now, would be replaced by Error handling
            throw new RuntimeException("The request isn't to Authorise or Reject, not proceeding further.");
        }
        return accountRequestStatusEnum;
    }

    private AccountRequestStatusHistory saveAccountRequestStatusHistory(AccountRequest savedAccountRequestInfo, String clientId, InternalUserRoleEnum role) {
        AccountRequestStatusHistory accountRequestStatusChangeHistory = new AccountRequestStatusHistory();
        accountRequestStatusChangeHistory.setAccountRequestInfoId(savedAccountRequestInfo.getAccountRequestIdentifier());
        accountRequestStatusChangeHistory.setResourceStatus(savedAccountRequestInfo.getAccountRequestStatus());
        accountRequestStatusChangeHistory.setStatusUpdatedBy(clientId);
        accountRequestStatusChangeHistory.setStatusUpdatedByRole(role);
        return accountRequestInfoHistoryRepository.save(accountRequestStatusChangeHistory);
    }

    private AccountRequestAssociatedAccountResource saveAssociatedAccountsResource(Long accountRequestInfoId, String accountIdentifier) {
        AccountRequestAssociatedAccountResource associatedAccountsResource = new AccountRequestAssociatedAccountResource();
        associatedAccountsResource.setAccountRequestInfoId(accountRequestInfoId);
        associatedAccountsResource.setAccountIdentifier(accountIdentifier);
        return associatedAccountsResourceRepository.save(associatedAccountsResource);
    }
}
