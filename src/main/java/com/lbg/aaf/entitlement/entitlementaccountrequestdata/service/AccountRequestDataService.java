package com.lbg.aaf.entitlement.entitlementaccountrequestdata.service;

import java.io.IOException;
import java.net.URISyntaxException;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.AccountRequestOutputData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.CreateAccountInputData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.UpdateAccountInputData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.UpdateRequestOutputData;

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

    /**
     * findByAccountRequestExternalIdentifierAndProviderClientId takes accountRequestId and clientId as input and will return the account request data associated with the request and client id
     * @param String accountRequestId
     * @param String clientId
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException
     */
    public AccountRequestOutputData findByAccountRequestExternalIdentifierAndProviderClientId(String accountRequestId, String clientId)
            throws IOException, URISyntaxException;

    /**
     * findByAccountRequestExternalIdentifier takes accountRequestId as input and will return the account request data associated with the request and client id
     * @param String accountRequestId
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException
     */
    public AccountRequestOutputData findByAccountRequestExternalIdentifier(String accountRequestId)
            throws IOException, URISyntaxException;

    /**
     * updateAccountRequestData UpdateAccountInputdata as input and will update the associated account request data associated with the request and client id
     * @param UpdateAccountInputData accountInputData
     * @param String accountRequestId
     * @param String clientId
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException, URISyntaxException
     */
    public UpdateRequestOutputData updateAccountRequestData(UpdateAccountInputData createAccountInputData, String accountRequestId, String clientId, String clientRole)
            throws IOException, URISyntaxException;
}
