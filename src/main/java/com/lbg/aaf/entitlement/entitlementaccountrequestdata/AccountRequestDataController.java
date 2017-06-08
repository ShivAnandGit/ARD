package com.lbg.aaf.entitlement.entitlementaccountrequestdata;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.AccountRequestOutputData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.CreateAccountInputData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.service.AccountRequestDataService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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
    @ApiOperation(value = "CreateAccountRequest", nickname = "CreateAccountRequest")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Success", response = AccountRequestDataController.class), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(value = "v1/accounts-requests", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Callable<ResponseEntity<AccountRequestOutputData>> createAccountRequests(
            @RequestHeader(value = "x-lbg-internal-user-role") final String internalUserRole,
            @RequestHeader(value = "x-lbg-txn-correlation-id") final String txnCorrelationId, @RequestHeader(value = "x-lbg-client-id") final String clientId,
            @RequestBody final CreateAccountInputData createAccountInputData, final HttpServletRequest request,
            HttpServletResponse response) {
        return new Callable<ResponseEntity<AccountRequestOutputData>>() {
            @Override
            public ResponseEntity<AccountRequestOutputData> call() throws Exception {
                return new ResponseEntity<AccountRequestOutputData>(accountRequestDataService.createAccountRequestData(createAccountInputData, clientId), HttpStatus.CREATED);
            }
        };
    }

    /**
     * Get an account request for provided query parameter
     * @return Callable<ResponseEntity<String>> List of accounts-requests
     */
    @ApiOperation(value = "GetAccountRequestByQueryParams", nickname = "GetAccountRequestByQueryParams")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = AccountRequestDataController.class), @ApiResponse(code = 400, message = "Bad Request", response = AccountRequestDataController.class), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(value = "v1/accounts-requests", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Callable<ResponseEntity<AccountRequestOutputData>> getAccountRequests(
            @RequestHeader(value = "x-lbg-internal-user-role") final String internalUserRole,
            @RequestHeader(value = "x-lbg-txn-correlation-id") final String txnCorrelationId, final HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required = true) final String accountRequestId,
            @RequestParam(required = true) final String clientId) {
        return new Callable<ResponseEntity<AccountRequestOutputData>>() {
            @Override
            public ResponseEntity<AccountRequestOutputData> call() throws Exception {
                return new ResponseEntity<AccountRequestOutputData>(accountRequestDataService.findByAccountRequestExternalIdentifierAndProviderClientId(accountRequestId, clientId), HttpStatus.OK);
            }
        };
    }

    /**
     * Get an account request for unique account request identifier
     * @return Callable<ResponseEntity<String>> accounts-request
     */
    @ApiOperation(value = "GetAccountRequestByRequestId", nickname = "GetAccountRequestByRequestId")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = AccountRequestDataController.class), @ApiResponse(code = 400, message = "Bad Request", response = AccountRequestDataController.class), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(value = "v1/accounts-requests/{accountRequestId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Callable<ResponseEntity<AccountRequestOutputData>> getAccountRequestForAccountId(
            @RequestHeader(value = "x-lbg-internal-user-role") final String internalUserRole,
            @RequestHeader(value = "x-lbg-txn-correlation-id") final String txnCorrelationId, final HttpServletRequest request, HttpServletResponse response,
            @PathVariable final String accountRequestId) {
        return new Callable<ResponseEntity<AccountRequestOutputData>>() {
            @Override
            public ResponseEntity<AccountRequestOutputData> call() throws Exception {
                return new ResponseEntity<AccountRequestOutputData>(accountRequestDataService.findByAccountRequestExternalIdentifier(accountRequestId), HttpStatus.OK);
            }
        };
    }

    /**
     * AISP can delete a previosly created account request (whether it was consented to or not). The
     * PSU may want to remove their consent with AISP instead of revoking authorisation with the
     * ASPSP
     * @return Callable<ResponseEntity<String>> accounts-request
     */
    @ApiOperation(value = "DeleteAccountRequest", nickname = "DeleteAccountRequest")
    @ApiResponses(value = {@ApiResponse(code = 204, message = "Success", response = AccountRequestDataController.class), @ApiResponse(code = 400, message = "Bad Request", response = AccountRequestDataController.class), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Failure")})
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
}