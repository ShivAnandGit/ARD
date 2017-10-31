package com.lbg.ob.aisp.accountrequestdata.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestOutputResponse;
import com.lbg.ob.aisp.accountrequestdata.data.CreateAccountInputRequest;
import com.lbg.ob.aisp.accountrequestdata.data.UpdateAccountRequestInputData;
import com.lbg.ob.aisp.accountrequestdata.data.UpdateAccountRequestOutputData;

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
     * @param UpdateAccountRequestInputData accountInputData
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
     * @param clientId
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException, URISyntaxException
     */
    public void revokeAccountRequestData(String accountRequestId, String clientRole, String txnCorrelationId, String clientId)
            throws IOException, URISyntaxException, ExecutionException, InterruptedException;

}
