package com.lbg.aaf.entitlement.entitlementaccountrequestdata.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.AccountRequestAssociatedAccountResource;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.AccountRequest;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.AccountRequestOutputData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.AccountRequestStatusHistory;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.AccountResource;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.CreateAccountInputData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.InternalUserRoleEnum;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository.AccountRequestAssociatedAccountRepository;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository.AccountRequestInfoRepository;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository.AccountRequestStatusChangeHistoryRepository;

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
            AccountRequestAssociatedAccountResource associatedAccountsResource = new AccountRequestAssociatedAccountResource();
            associatedAccountsResource.setAccountRequestInfoId(savedAccountRequestInfo.getAccountRequestIdentifier());
            associatedAccountsResource.setAccountIdentifier(selectedAccount.getAccount().getIdentification());
            accountIds.add(selectedAccount.getAccount().getIdentification());
            associatedAccountsResourceRepository.save(associatedAccountsResource);
        }

        AccountRequestStatusHistory accountRequestStatusChangeHistory = new AccountRequestStatusHistory();
        accountRequestStatusChangeHistory.setAccountRequestInfoId(savedAccountRequestInfo.getAccountRequestIdentifier());
        accountRequestStatusChangeHistory.setResourceStatus(savedAccountRequestInfo.getAccountRequestStatus());
        accountRequestStatusChangeHistory.setStatusUpdatedBy(savedAccountRequestInfo.getProviderClientId());
        accountRequestStatusChangeHistory.setStatusUpdatedByRole(InternalUserRoleEnum.CUSTOMER);
        accountRequestInfoHistoryRepository.save(accountRequestStatusChangeHistory);

        AccountRequestOutputData accountRequestOutputData = new AccountRequestOutputData(savedAccountRequestInfo.getAccountRequestJsonString(), accountIds);
        return accountRequestOutputData;

    }

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
}
