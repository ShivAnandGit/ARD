package com.lbg.aaf.entitlement.entitlementaccountrequestdata.service;

import java.io.IOException;
import java.net.URISyntaxException;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.*;

public interface AccountRequestDataService<T> {

    /**
     * createEntitlement takes createEntitlementInput as input and will create entitlement for each
     * request .
     * @param CreateEntitlementInput createEntitlementInput
     * @return CreateEntilementOutputData EntitlementAccessCode
     * @throws IOException
     * @throws URISyntaxException
     */
    public AccountRequestOutputResponse createAccountRequestData(CreateAccountInputRequest createAccountInputRequest, String clientId, String fapiFinancialId, String txnCorrelationId)
            throws IOException, URISyntaxException, InterruptedException;

    /**
     * findByAccountRequestExternalIdentifierAndProviderClientId takes accountRequestId and clientId as input and will return the account request data associated with the request and client id
     * @param String accountRequestId
     * @param String clientId
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException
     */
    public AccountRequestOutputResponse findByAccountRequestExternalIdentifierAndProviderClientId(String accountRequestId, String clientId, String txnCorrelationId)
            throws IOException, URISyntaxException;

    /**
     * findByAccountRequestExternalIdentifier takes accountRequestId as input and will return the account request data associated with the request and client id
     * @param String accountRequestId
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException
     */
    public AccountRequestOutputResponse findByAccountRequestExternalIdentifier(String accountRequestId, String txnCorrelationId)
            throws IOException, URISyntaxException;

    /**
     * updateAccountRequestData UpdateAccountInputdata as input and will update the associated account request data associated with the request and client id
     * @param UpdateAccountInputData accountInputData
     * @param String accountRequestId
     * @param String clientId
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException, URISyntaxException
     */
    public UpdateAccountRequestOutputData updateAccountRequestData(UpdateAccountRequestInputData createAccountInputData, String accountRequestId, String clientRole, String txnCorrelationId)
            throws IOException, URISyntaxException;

    /**
     * revokeAccountRequestData  will take accountRequestId as input and will update the status of associated account request data as "Revoked". the Entitlement API would also
     * be called to revoke the entitlement.
     * @param String accountRequestId
     * @param String clientId
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException, URISyntaxException
     */
    public void revokeAccountRequestData(String accountRequestId, String clientRole, String txnCorrelationId)
            throws IOException, URISyntaxException;

}
