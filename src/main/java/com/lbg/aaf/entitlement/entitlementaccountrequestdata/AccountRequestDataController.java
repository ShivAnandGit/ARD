package com.lbg.aaf.entitlement.entitlementaccountrequestdata;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.service.AccountRequestDataService;

import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.AccountRequestDataConstant.*;


@RestController
public final class AccountRequestDataController {

    Logger logger = Logger.getLogger(AccountRequestDataController.class.getSimpleName());

    @Autowired
    AccountRequestDataService<?> accountRequestDataService;

    /**
     * Create an account request
     * @return Callable<AccountRequestOutputData> List of accounts-requests
     */
    @RequestMapping(value = "v1/accounts-requests", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Callable<AccountRequestOutputResponse> createAccountRequests(
            @RequestHeader(value = X_LBG_INTERNAL_USER_ROLE) final String internalUserRole,
            @RequestHeader(value = X_LBG_TXN_CORRELATION_ID) final String txnCorrelationId,
            @RequestHeader(value = X_LBG_CLIENT_ID) final String clientId,
            @RequestHeader(value = X_FAPI_FINANCIAL_ID) final String financialId,
            @RequestHeader(value = X_FAPI_INTERACTION_ID, required = false) final String interactionId,
            @Valid @RequestBody final CreateAccountInputRequest createAccountInputRequest, final HttpServletRequest request,
            HttpServletResponse response) {
        if(!StringUtils.isEmpty(interactionId)) {
            response.setHeader(X_FAPI_INTERACTION_ID, interactionId);
        }
        return () ->accountRequestDataService.createAccountRequestData(createAccountInputRequest, clientId, financialId);
    }

    /**
     * Get an account request for provided query parameter
     * @return Callable<AccountRequestOutputData> List of accounts-requests
     */
    @RequestMapping(value = "v1/accounts-requests", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Callable<AccountRequestOutputResponse> getAccountRequests(
            @RequestHeader(value = X_LBG_INTERNAL_USER_ROLE) final String internalUserRole,
            @RequestHeader(value = X_LBG_TXN_CORRELATION_ID) final String txnCorrelationId, final HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required = true) final String accountRequestId,
            @RequestParam(required = true) final String clientId) {
        return () -> accountRequestDataService.findByAccountRequestExternalIdentifierAndProviderClientId(accountRequestId, clientId);
    }

    /**
     * Get an account request for unique account request identifier
     * @return Callable<AccountRequestOutputData> accounts-request
     */
    @RequestMapping(value = "v1/accounts-requests/{accountRequestId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Callable<AccountRequestOutputResponse> getAccountRequestForAccountId(
            @RequestHeader(value = X_LBG_INTERNAL_USER_ROLE) final String internalUserRole,
            @RequestHeader(value = X_LBG_TXN_CORRELATION_ID) final String txnCorrelationId,
            @RequestHeader(value = X_LBG_CLIENT_ID, required = false) final String clientId,
            @RequestHeader(value = X_FAPI_FINANCIAL_ID, required = false) final String financialId,
            @RequestHeader(value = X_FAPI_INTERACTION_ID, required = false) final String interactionId,
            final HttpServletRequest request, HttpServletResponse response,
            @PathVariable final String accountRequestId) {
        if(!StringUtils.isEmpty(interactionId)) {
            response.setHeader(X_FAPI_INTERACTION_ID, interactionId);
        }
        return () -> accountRequestDataService.findByAccountRequestExternalIdentifier(accountRequestId);
    }

    /**
     * AISP can delete a previosly created account request (whether it was consented to or not). The
     * PSU may want to revoke their consent with AISP instead of revoking authorisation with the
     * ASPSP
     * @return Callable<Boolean> accounts-request
     */
    @RequestMapping(value = "v1/accounts-requests/{accountRequestId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccountRequestForAccountId(
            @RequestHeader(value = X_LBG_INTERNAL_USER_ROLE) final String internalUserRole,
            @RequestHeader(value = X_LBG_TXN_CORRELATION_ID) final String txnCorrelationId,
            @RequestHeader(value = X_LBG_CLIENT_ID) final String clientId,
            @RequestHeader(value = X_FAPI_FINANCIAL_ID) final String financialId,
            @RequestHeader(value = X_FAPI_INTERACTION_ID, required = false) final String interactionId,
            final HttpServletRequest request, HttpServletResponse response,
            @PathVariable final String accountRequestId) throws IOException, URISyntaxException {
        if(!StringUtils.isEmpty(interactionId)) {
            response.setHeader(X_FAPI_INTERACTION_ID, interactionId);
        }
        accountRequestDataService.revokeAccountRequestData(accountRequestId, internalUserRole, txnCorrelationId);
    }

    /**
     * AISP can Update a previously created account request (Authorised, Rejected).
     * @return Callable<UpdateAccountRequestOutputData> updatedRequestOuput
     */
    @RequestMapping(value = "v1/accounts-requests/{accountRequestId}/status", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Callable<UpdateAccountRequestOutputData> updateAccountRequestForRequestId(
            @RequestHeader(value = X_LBG_INTERNAL_USER_ROLE) final String internalUserRole,
            @RequestHeader(value = X_LBG_TXN_CORRELATION_ID) final String txnCorrelationId,
            final HttpServletRequest request, HttpServletResponse response,
            @PathVariable final String accountRequestId,
            @RequestBody UpdateAccountRequestInputData inputData) throws Exception{
        return () -> accountRequestDataService.updateAccountRequestData(inputData, accountRequestId, internalUserRole);
    }
}