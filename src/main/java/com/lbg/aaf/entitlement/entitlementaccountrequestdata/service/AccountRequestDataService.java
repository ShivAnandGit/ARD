package com.lbg.aaf.entitlement.entitlementaccountrequestdata.service;

import java.io.IOException;
import java.net.URISyntaxException;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.AccountRequestOutputData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.CreateAccountInputData;

/**
 * Interface AccountRequestDataService.All Service Class will implement this interface.
 * @author Amit Jain
 */
public interface AccountRequestDataService<T> {

    /**
     * createEntitlement takes createEntitlementInput as input and will create entitlement for each
     * request .
     * @param CreateEntitlementInput createEntitlementInput
     * @return CreateEntilementOutputData EntitlementAccessCode
     * @throws IOException
     * @throws URISyntaxException
     */
    public AccountRequestOutputData createAccountRequestData(CreateAccountInputData createAccountInputData, String clientId)
            throws IOException, URISyntaxException;

    public AccountRequestOutputData findByAccountRequestExternalIdentifierAndProviderClientId(String accountRequestId, String clientId)
            throws IOException, URISyntaxException;

    public AccountRequestOutputData findByAccountRequestExternalIdentifier(String accountRequestId)
            throws IOException, URISyntaxException;
}
