package com.lbg.ob.aisp.accountrequestdata.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequest;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestOutputResponse;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestStatusEnum;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestStatusHistory;
import com.lbg.ob.aisp.accountrequestdata.data.CreateAccountInputRequest;
import com.lbg.ob.aisp.accountrequestdata.data.InternalUserRoleEnum;
import com.lbg.ob.aisp.accountrequestdata.data.UpdateAccountRequestInputData;
import com.lbg.ob.aisp.accountrequestdata.data.UpdateAccountRequestOutputData;
import com.lbg.ob.aisp.accountrequestdata.exception.EntitlementUpdateFailedException;
import com.lbg.ob.aisp.accountrequestdata.exception.InvalidRequestException;
import com.lbg.ob.aisp.accountrequestdata.exception.RecordNotFoundException;
import com.lbg.ob.aisp.accountrequestdata.exception.ResourceAccessException;
import com.lbg.ob.aisp.accountrequestdata.exception.handler.ErrorData;
import com.lbg.ob.aisp.accountrequestdata.util.AccountRequestDataConstant;
import com.lbg.ob.aisp.accountrequestdata.util.StateChangeMachine;
import com.lbg.ob.aisp.accountrequestdata.util.Util;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import static com.lbg.ob.aisp.accountrequestdata.exception.ExceptionConstants.ARD_API_ERR_007;
import static com.lbg.ob.aisp.accountrequestdata.exception.ExceptionConstants.ARD_API_ERR_503;
import static com.lbg.ob.aisp.accountrequestdata.exception.ExceptionConstants.BAD_REQUEST_INVALID_REQUEST;

/**
 * Class AccountRequestDataServiceImpl.All Service Class will implement this interface.
 * @author Amit Jain
 */

@Service
public class AccountRequestDataServiceImpl<T> implements AccountRequestDataService<T> {

    @Autowired
    private EntitlementProxyService entitlementService;

    @Autowired
    private StateChangeMachine stateChangeMachine;

    @Autowired
    private AccountRequestDAO accountRequestDAO;

    private static final Logger logger = LogManager.getLogger(EntitlementProxyServiceImpl.class);

    /**
     * createAccountRequestData takes CreateAccountInputData as input and will create entitlement
     * for each request .
     * @param CreateAccountInputData createAccountInputRequest
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException
     * @throws URISyntaxException
     */
    @HystrixCommand(commandKey = "database", fallbackMethod = "fallbackCreate")
    @Override
    public AccountRequestOutputResponse createAccountRequestData(CreateAccountInputRequest createAccountInputRequest, String clientId, String fapiFinancialId)
            throws IOException, URISyntaxException, InterruptedException {
        logger.trace( "ENTRY --> createAccountRequestData");
        String json = getJsonRequest(createAccountInputRequest);
        AccountRequest accountRequestInfo = new AccountRequest(createAccountInputRequest.getCreateAccountInputData(), clientId, fapiFinancialId, json);
        AccountRequestOutputResponse accountRequestOutputResponse = accountRequestDAO.createAccountRequest(accountRequestInfo);
        logger.trace("createAccountRequestData <-- EXIT");
        return accountRequestOutputResponse;
    }

    private String getJsonRequest(CreateAccountInputRequest request) throws IOException {
        logger.debug("Getting JSON Request");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(request);
    }

    /**
     * findByAccountRequestExternalIdentifierAndProviderClientId takes accountRequestId and clientId
     * as input and will return the account request data associated with the request and client id
     * @param String accountRequestId
     * @param String clientId
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException
     */
    @HystrixCommand(commandKey = "database", fallbackMethod = "fallbackFindWithClientId", ignoreExceptions = {RecordNotFoundException.class})
    public AccountRequestOutputResponse findByAccountRequestExternalIdentifierAndProviderClientId(String accountRequestId, String clientId) throws IOException {
        logger.trace( "ENTRY --> findByAccountRequestExternalIdentifierAndProviderClientId");
        AccountRequestOutputResponse accountRequestOutputResponse = accountRequestDAO.findAccountRequest(accountRequestId, clientId);
        logger.trace("findByAccountRequestExternalIdentifierAndProviderClientId <-- EXIT");
        return accountRequestOutputResponse;
    }

    /**
     * findByAccountRequestExternalIdentifier takes accountRequestId as input and will return the
     * account request data associated with the request and client id
     * @param accountRequestId
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException
     */

    @HystrixCommand(commandKey = "database", fallbackMethod = "fallbackFind", ignoreExceptions = {RecordNotFoundException.class})
    public AccountRequestOutputResponse findByAccountRequestExternalIdentifier(String accountRequestId) throws IOException {
        logger.trace( "ENTRY --> findByAccountRequestExternalIdentifier");
        AccountRequestOutputResponse accountRequestOutputResponse = accountRequestDAO.findAccountRequest(accountRequestId);
        logger.trace("findByAccountRequestExternalIdentifier <-- EXIT");
        return accountRequestOutputResponse;
    }

    /**
     * updateAccountRequestData UpdateAccountInputdata as input and will update the associated
     * account request data associated with the request and client id
     * @param accountInputData accountInputData
     * @param accountRequestId
     * @param clientRole
     * @return AccountRequestOutputData accountRequestOutputData
     * @throws IOException, URISyntaxException
     */
    @HystrixCommand(commandKey = "database", fallbackMethod = "fallbackUpdate", ignoreExceptions = {InvalidRequestException.class, RecordNotFoundException.class})
    @Override
    public UpdateAccountRequestOutputData updateAccountRequestData(UpdateAccountRequestInputData accountInputData, String accountRequestId, String clientRole)
            throws IOException, URISyntaxException {
        logger.trace( "ENTRY --> updateAccountRequestData");
        AccountRequest accountRequestInfo = accountRequestDAO.getAccountRequest(accountRequestId);
        String possibleStatus = accountInputData.getStatus();
        if (!(AccountRequestDataConstant.AUTHORISED.equalsIgnoreCase(possibleStatus) || AccountRequestDataConstant.REJECTED.equalsIgnoreCase(possibleStatus)
        		||AccountRequestDataConstant.REVOKED.equalsIgnoreCase(possibleStatus))) {
            throw new InvalidRequestException(BAD_REQUEST_INVALID_REQUEST, ARD_API_ERR_007);
        }
        AccountRequestStatusEnum accountRequestStatusEnum = stateChangeMachine.getUpdatableStatus(accountRequestInfo.getAccountRequestStatus(), possibleStatus);
        //update the status in ACCT_REQUEST
        accountRequestInfo.setAccountRequestStatus(accountRequestStatusEnum);
        if (accountRequestStatusEnum.equals(AccountRequestStatusEnum.AUTHORISED)) {
            Long entitlementId = accountInputData.getEntitlementId();
            String entitlementAccessCode = accountInputData.getEntitlementAccessCode();
            checkMandatoryVariablesPresent(entitlementId, entitlementAccessCode);
            accountRequestInfo.setEntitlementId(entitlementId);
            accountRequestInfo.setEntitlementAccessCode(entitlementAccessCode);
        }
        //update the history
        InternalUserRoleEnum role = InternalUserRoleEnum.valueOf(clientRole.toUpperCase());

        AccountRequestStatusHistory savedAccountRequestStatusHistory = accountRequestDAO.updateAccountRequest(accountRequestInfo, role);

        UpdateAccountRequestOutputData updateAccountRequestOutputData = new UpdateAccountRequestOutputData();
        updateAccountRequestOutputData.setAccountRequestId(accountRequestId);
        updateAccountRequestOutputData.setUpdatedAtTimestamp(Util.formatDateAsISO8601(savedAccountRequestStatusHistory.getStatusUpdatedDateTime().toLocalDateTime()));
        updateAccountRequestOutputData.setUpdatedStatus(accountRequestStatusEnum.getValue());
        logger.trace("updateAccountRequestData <-- EXIT");
        return updateAccountRequestOutputData;
    }


    @HystrixCommand(commandKey = "database", fallbackMethod = "fallbackRevoke", ignoreExceptions = {RecordNotFoundException.class, EntitlementUpdateFailedException.class, InvalidRequestException.class})
    @Override
    public void revokeAccountRequestData(String accountRequestId, String clientRole, String clientId, Boolean fovIndicator, HttpHeaders headers)
            throws IOException, URISyntaxException, ExecutionException, InterruptedException {
        logger.trace( "ENTRY --> revokeAccountRequestData");
        AccountRequest accountRequestInfo = accountRequestDAO.getAccountRequest(accountRequestId);
        String accountRequestStatus = accountRequestInfo.getAccountRequestStatus();
        AccountRequestStatusEnum updateableStatus = stateChangeMachine.getUpdatableStatus(accountRequestStatus, AccountRequestStatusEnum.REVOKED);
        //call the entitlement API to revoke the entitlement, if the status was authorised
        if (accountRequestStatus.equals(AccountRequestDataConstant.AUTHORISED)) {
            Long entitlementId = accountRequestInfo.getEntitlementId();
            entitlementService.revokeEntitlement(entitlementId, InternalUserRoleEnum.SYSTEM.toString(), clientRole, clientId, fovIndicator,headers);
        }
        accountRequestDAO.revokeAccountRequest(clientRole, accountRequestInfo, updateableStatus);
        logger.trace("revokeAccountRequestData <-- EXIT");
    }

    private AccountRequestOutputResponse fallbackCreate(CreateAccountInputRequest createAccountInputRequest, String clientId, String fapiFinancialId, Throwable ex) { //NOSONAR
        logger.error(ex); //NOSONAR
        ErrorData errorData = new ErrorData(Long.valueOf(HttpStatus.SERVICE_UNAVAILABLE.toString()), ARD_API_ERR_503, ex.getMessage()); //NOSONAR
        throw new ResourceAccessException(errorData, ex); //NOSONAR
    }

    private AccountRequestOutputResponse fallbackFindWithClientId(String accountRequestId, String clientId, Throwable ex) { //NOSONAR
        logger.error(ex); //NOSONAR
        ErrorData errorData = new ErrorData(Long.valueOf(HttpStatus.SERVICE_UNAVAILABLE.toString()), ARD_API_ERR_503, ex.getMessage()); //NOSONAR
        throw new ResourceAccessException(errorData, ex); //NOSONAR
    }

    private AccountRequestOutputResponse fallbackFind(String accountRequestId, Throwable ex) { //NOSONAR
        logger.error(ex); //NOSONAR
        ErrorData errorData = new ErrorData(Long.valueOf(HttpStatus.SERVICE_UNAVAILABLE.toString()), ARD_API_ERR_503, ex.getMessage()); //NOSONAR
        throw new ResourceAccessException(errorData, ex); //NOSONAR
    }

    private void fallbackRevoke(String accountRequestId, String clientRole, String clientId, Boolean fovIndicator, Throwable ex) { //NOSONAR
        logger.error(ex); //NOSONAR
        ErrorData errorData = new ErrorData(Long.valueOf(HttpStatus.SERVICE_UNAVAILABLE.toString()), ARD_API_ERR_503, ex.getMessage()); //NOSONAR
        throw new ResourceAccessException(errorData, ex); //NOSONAR
    }

    private UpdateAccountRequestOutputData fallbackUpdate(UpdateAccountRequestInputData accountInputData, String accountRequestId, String clientRole, Throwable ex) {//NOSONAR
        //        logger.error(ex);                                                                                                                                            //NOSONAR
        ErrorData errorData = new ErrorData(Long.valueOf(HttpStatus.SERVICE_UNAVAILABLE.toString()), ARD_API_ERR_503, ex.getMessage()); //NOSONAR
        throw new ResourceAccessException(errorData, ex); //NOSONAR
    }
    
	private void checkMandatoryVariablesPresent(Long entitlementId, String entitlementAccessCode) {
		if (entitlementId == null || entitlementId == 0 || StringUtils.isEmpty(entitlementAccessCode)) {
		    throw new InvalidRequestException("Request can't be authorised without an Entitlement Id or EntitlementAccessCode", "ARD_API_ERR_999");
		}
	}

}
