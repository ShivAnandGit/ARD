package com.lbg.ob.aisp.accountrequestdata.controller;

import static com.lbg.ob.aisp.accountrequestdata.util.AccountRequestDataConstant.X_FAPI_FINANCIAL_ID;
import static com.lbg.ob.aisp.accountrequestdata.util.AccountRequestDataConstant.X_FAPI_INTERACTION_ID;
import static com.lbg.ob.aisp.accountrequestdata.util.AccountRequestDataConstant.X_LBG_CLIENT_ID;
import static com.lbg.ob.aisp.accountrequestdata.util.AccountRequestDataConstant.X_LBG_FOV_INDICATOR;
import static com.lbg.ob.aisp.accountrequestdata.util.AccountRequestDataConstant.X_LBG_INTERNAL_USER_ROLE;
import static com.lbg.ob.aisp.accountrequestdata.util.AccountRequestDataConstant.X_LBG_TXN_CORRELATION_ID;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.core.lbg.security.annotation.IsAllowed;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestOutputResponse;
import com.lbg.ob.aisp.accountrequestdata.data.CreateAccountInputRequest;
import com.lbg.ob.aisp.accountrequestdata.data.UpdateAccountRequestInputData;
import com.lbg.ob.aisp.accountrequestdata.data.UpdateAccountRequestOutputData;
import com.lbg.ob.aisp.accountrequestdata.service.AccountRequestDataService;

@RestController
public final class AccountRequestDataController {

    @Autowired
    private AccountRequestDataService<?> accountRequestDataService;
    
    private static final Logger logger = LogManager.getLogger(AccountRequestDataController.class);

    /**
     * Create an account request
     * @return Callable<AccountRequestOutputData> List of accounts-requests
     */
    @IsAllowed(role = {"SYSTEM"})
    @RequestMapping(value = "/accounts-requests", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Callable<AccountRequestOutputResponse> createAccountRequests(
            @RequestHeader(value = X_LBG_INTERNAL_USER_ROLE) final String internalUserRole,
            @RequestHeader(value = X_LBG_TXN_CORRELATION_ID) final String txnCorrelationId,
            @RequestHeader(value = X_LBG_CLIENT_ID) final String clientId,
            @RequestHeader(value = X_FAPI_FINANCIAL_ID) final String financialId,
            @RequestHeader(value = X_FAPI_INTERACTION_ID, required = false) final String interactionId,
            @RequestHeader(value = X_LBG_FOV_INDICATOR, required = false) final Boolean fovIndicator,
            @Valid @RequestBody final CreateAccountInputRequest createAccountInputRequest, final HttpServletRequest request,
            HttpServletResponse response) {
        return () -> {
            logger.trace("ENTRY --> createAccountRequests");
            if(!StringUtils.isEmpty(interactionId)) {
                response.setHeader(X_FAPI_INTERACTION_ID, interactionId);
            }
            AccountRequestOutputResponse accountRequestData = accountRequestDataService.createAccountRequestData(createAccountInputRequest, clientId, financialId);
            logger.trace("EXIT --> createAccountRequests");
            return accountRequestData;
        };
    }

    /**
     * Get an account request for provided query parameter
     * @return Callable<AccountRequestOutputData> List of accounts-requests
     */
    @IsAllowed(role = {"CUSTOMER"})
    @RequestMapping(value = "/accounts-requests", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Callable<AccountRequestOutputResponse> getAccountRequests(
            @RequestHeader(value = X_LBG_INTERNAL_USER_ROLE) final String internalUserRole,
            @RequestHeader(value = X_LBG_TXN_CORRELATION_ID) final String txnCorrelationId, 
            @RequestHeader(value = X_LBG_FOV_INDICATOR, required = false) final Boolean fovIndicator,
            final HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required = true) final String accountRequestId,
            @RequestParam(required = true) final String clientId) {
        return () -> {
            logger.trace("ENTRY --> getAccountRequests");
            AccountRequestOutputResponse accountRequestData = accountRequestDataService.findByAccountRequestExternalIdentifierAndProviderClientId(accountRequestId, clientId);
            logger.trace("EXIT --> getAccountRequests");
            return accountRequestData;
        };
    }

    /**
     * Get an account request for unique account request identifier
     * @return Callable<AccountRequestOutputData> accounts-request
     */
    @IsAllowed(role = {"SYSTEM"})
    @RequestMapping(value = "/accounts-requests/{accountRequestId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Callable<AccountRequestOutputResponse> getAccountRequestForAccountId(
            @RequestHeader(value = X_LBG_INTERNAL_USER_ROLE) final String internalUserRole,
            @RequestHeader(value = X_LBG_TXN_CORRELATION_ID) final String txnCorrelationId,
            @RequestHeader(value = X_LBG_CLIENT_ID, required = false) final String clientId,
            @RequestHeader(value = X_FAPI_FINANCIAL_ID, required = false) final String financialId,
            @RequestHeader(value = X_FAPI_INTERACTION_ID, required = false) final String interactionId,
            @RequestHeader(value = X_LBG_FOV_INDICATOR, required = false) final Boolean fovIndicator,
            final HttpServletRequest request, HttpServletResponse response,
            @PathVariable final String accountRequestId) {
        return () -> {
            logger.trace("ENTRY --> getAccountRequestForAccountId");
            if(!StringUtils.isEmpty(interactionId)) {
                response.setHeader(X_FAPI_INTERACTION_ID, interactionId);
            }
            AccountRequestOutputResponse accountRequestData = accountRequestDataService.findByAccountRequestExternalIdentifier(accountRequestId);
            logger.trace("EXIT --> getAccountRequestForAccountId");
            return accountRequestData;
        };
    }

    /**
     * AISP can delete a previosly created account request (whether it was consented to or not). The
     * PSU may want to revoke their consent with AISP instead of revoking authorisation with the
     * ASPSP
     * @return Callable<Boolean> accounts-request
     */
    @IsAllowed(role = {"SYSTEM"})
    @RequestMapping(value = "/accounts-requests/{accountRequestId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
        @ResponseStatus(HttpStatus.NO_CONTENT)
    public Callable<Void> deleteAccountRequestForAccountId(
            @RequestHeader(value = X_LBG_INTERNAL_USER_ROLE) final String internalUserRole,
            @RequestHeader(value = X_LBG_TXN_CORRELATION_ID) final String txnCorrelationId,
            @RequestHeader(value = X_LBG_CLIENT_ID) final String clientId,
            @RequestHeader(value = X_FAPI_FINANCIAL_ID) final String financialId,
            @RequestHeader(value = X_FAPI_INTERACTION_ID, required = false) final String interactionId,
            @RequestHeader(value = X_LBG_FOV_INDICATOR, required = false) final Boolean fovIndicator,
            @RequestHeader HttpHeaders headers,
            final HttpServletRequest request, HttpServletResponse response,
            @PathVariable final String accountRequestId) throws IOException, URISyntaxException, ExecutionException, InterruptedException {
        return () -> {
            logger.trace("ENTRY --> deleteAccountRequestForAccountId");
            if (!StringUtils.isEmpty(interactionId)) {
                response.setHeader(X_FAPI_INTERACTION_ID, interactionId);
            }
            accountRequestDataService.revokeAccountRequestData(accountRequestId, internalUserRole, clientId, fovIndicator,headers);
            return null;
        };
    }

    /**
     * AISP can Update a previously created account request (Authorised, Rejected).
     * @return Callable<UpdateAccountRequestOutputData> updatedRequestOuput
     */
    @IsAllowed(role = {"CUSTOMER", "BRANCH_COLLEAGUE", "SUPER_USER", "SYSTEM"})
    @RequestMapping(value = "/accounts-requests/{accountRequestId}/status", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Callable<UpdateAccountRequestOutputData> updateAccountRequestForRequestId(
            @RequestHeader(value = X_LBG_INTERNAL_USER_ROLE) final String internalUserRole,
            @RequestHeader(value = X_LBG_TXN_CORRELATION_ID) final String txnCorrelationId,
            @RequestHeader(value = X_LBG_FOV_INDICATOR, required = false) final Boolean fovIndicator,
            final HttpServletRequest request, HttpServletResponse response,
            @PathVariable final String accountRequestId,
            @RequestBody UpdateAccountRequestInputData inputData) {
        return () -> {
            logger.trace("ENTRY --> updateAccountRequestForRequestId");
            UpdateAccountRequestOutputData updateAccountRequestOutputData = accountRequestDataService.updateAccountRequestData(inputData, accountRequestId, internalUserRole);
            logger.trace("EXIT --> updateAccountRequestForRequestId");
            return updateAccountRequestOutputData;
        };
    }
}