package com.lbg.aaf.entitlement.entitlementaccountrequestdata;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.UpdateAccountInputData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.UpdateRequestOutputData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.AccountRequestOutputData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.CreateAccountInputData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.service.AccountRequestDataService;

/**
 * Controller class to receive Rest Calls. Uses @Controller
 * @author Amit Jain
 */
@RestController
public final class AccountRequestDataController {

    Logger logger = Logger.getLogger(AccountRequestDataController.class.getSimpleName());

    @Autowired
    AccountRequestDataService<?> accountRequestDataService;

    /**
     * Create an account request
     * @return Callable<ResponseEntity<AccountRequestOutputData>> List of accounts-requests
     */
    @RequestMapping(value = "v1/accounts-requests", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Callable<AccountRequestOutputData> createAccountRequests(
            @RequestHeader(value = "x-lbg-internal-user-role") final String internalUserRole,
            @RequestHeader(value = "x-lbg-txn-correlation-id") final String txnCorrelationId, @RequestHeader(value = "x-lbg-client-id") final String clientId,
            @RequestBody final CreateAccountInputData createAccountInputData, final HttpServletRequest request,
            HttpServletResponse response) {
        return () ->accountRequestDataService.createAccountRequestData(createAccountInputData, clientId);
    }

    /**
     * Get an account request for provided query parameter
     * @return Callable<ResponseEntity<String>> List of accounts-requests
     */
    @RequestMapping(value = "v1/accounts-requests", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Callable<AccountRequestOutputData> getAccountRequests(
            @RequestHeader(value = "x-lbg-internal-user-role") final String internalUserRole,
            @RequestHeader(value = "x-lbg-txn-correlation-id") final String txnCorrelationId, final HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required = true) final String accountRequestId,
            @RequestParam(required = true) final String clientId) {
        return () -> accountRequestDataService.findByAccountRequestExternalIdentifierAndProviderClientId(accountRequestId, clientId);
    }

    /**
     * Get an account request for unique account request identifier
     * @return Callable<ResponseEntity<String>> accounts-request
     */
    @RequestMapping(value = "v1/accounts-requests/{accountRequestId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Callable<AccountRequestOutputData> getAccountRequestForAccountId(
            @RequestHeader(value = "x-lbg-internal-user-role") final String internalUserRole,
            @RequestHeader(value = "x-lbg-txn-correlation-id") final String txnCorrelationId, final HttpServletRequest request, HttpServletResponse response,
            @PathVariable final String accountRequestId) {
        return () -> accountRequestDataService.findByAccountRequestExternalIdentifier(accountRequestId);
    }

    /**
     * AISP can delete a previosly created account request (whether it was consented to or not). The
     * PSU may want to remove their consent with AISP instead of revoking authorisation with the
     * ASPSP
     * @return Callable<ResponseEntity<String>> accounts-request
     */
    @RequestMapping(value = "v1/accounts-requests/{accountRequestId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Callable<ResponseEntity<Void>> deleteAccountRequestForAccountId(
            @RequestHeader(value = "x-lbg-internal-user-role") final String internalUserRole,
            @RequestHeader(value = "x-lbg-txn-correlation-id") final String txnCorrelationId, final HttpServletRequest request, HttpServletResponse response,
            @PathVariable final String accountRequestId) {
        return new Callable<ResponseEntity<Void>>() {
            @Override
            public ResponseEntity<Void> call() throws Exception {
                return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
            }
        };
    }

    /**
     * AISP can Update a previously created account request (Authenticated, Rejected).
     * @return Callable<ResponseEntity<UpdateRequestOutputData>> updatedRequestOuput
     */
    @RequestMapping(value = "v1/accounts-requests/{accountRequestId}/status", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Callable<UpdateRequestOutputData> updateAccountRequestForRequestId(
            @RequestHeader(value = "x-lbg-internal-user-role") final String internalUserRole,
            @RequestHeader(value = "x-lbg-txn-correlation-id") final String txnCorrelationId,
            @RequestHeader(value = "x-lbg-client-id") final String clientId, final HttpServletRequest request, HttpServletResponse response,
            @PathVariable final String accountRequestId,
            @RequestBody UpdateAccountInputData inputData) throws Exception{
        return () -> accountRequestDataService.updateAccountRequestData(inputData, accountRequestId, clientId, internalUserRole);
    }
}