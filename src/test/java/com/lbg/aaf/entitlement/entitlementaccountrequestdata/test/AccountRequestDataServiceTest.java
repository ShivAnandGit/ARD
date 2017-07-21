package com.lbg.aaf.entitlement.entitlementaccountrequestdata.test;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.*;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.InvalidRequestException;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.RecordNotFoundException;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.service.AccountRequestDAO;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.service.AccountRequestDataServiceImpl;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.service.EntitlementProxyService;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.AccountRequestDataConstant;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.StateChangeMachine;
import com.lbg.ob.logger.Logger;
import com.lbg.ob.logger.factory.LoggerFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;

import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.ExceptionConstants.ARD_API_ERR_007;
import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.ExceptionConstants.BAD_REQUEST_INVALID_REQUEST;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ LoggerFactory.class,  RequestContextHolder.class, ServletRequestAttributes.class})
public class AccountRequestDataServiceTest {

    @Mock
    AccountRequestDAO accountRequestDAO;

    @Mock
    Logger LOGGER;

    @Mock
    EntitlementProxyService entitlementService;

    @Mock
    StateChangeMachine stateChangeMachine;

    @InjectMocks
    AccountRequestDataServiceImpl accountRequestDataService;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldUpdateAccountRequestDataForAuthorisedRequest() throws IOException, URISyntaxException, InvalidRequestException {
        UpdateAccountRequestInputData accountInputData = new UpdateAccountRequestInputData();
        accountInputData.setStatus("Authorised");
        accountInputData.setEntitlementId(123L);
        String testRequestId = "TestRequestId";
        String testClientId = "TestClientId";
        String testClientRole = "CUSTOMER";
        String txnCorrelationId = "correlationId";
        long accountRequestIdentifier = 1234L;
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        t.setStatusUpdatedDateTime();
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenReturn(accountRequest);
        when(accountRequestDAO.updateAccountRequest(accountRequest, InternalUserRoleEnum.CUSTOMER)).thenReturn(t);
        when(stateChangeMachine.getUpdatableStatus(anyString(), anyString())).thenReturn(AccountRequestStatusEnum.AUTHORISED);
        UpdateAccountRequestOutputData updateAccountRequestOutputData = accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, testClientRole, txnCorrelationId);
        assertNotNull(updateAccountRequestOutputData);

    }

    @Test(expected = InvalidRequestException.class)
    public void shouldThrowExceptionWhenUpdateForAuthorisedWithoutEntitlementId() throws IOException, URISyntaxException, InvalidRequestException {
        UpdateAccountRequestInputData accountInputData = new UpdateAccountRequestInputData();
        accountInputData.setStatus("Authorised");
        String testRequestId = "TestRequestId";
        String testClientId = "TestClientId";
        String testClientRole = "CUSTOMER";
        String txnCorrelationId = "correlationId";

        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenReturn(accountRequest);
        when(stateChangeMachine.getUpdatableStatus(anyString(), anyString())).thenReturn(AccountRequestStatusEnum.AUTHORISED);
        UpdateAccountRequestOutputData updateAccountRequestOutputData = accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, testClientRole, txnCorrelationId);
        assertNotNull(updateAccountRequestOutputData);

    }

    @Test
    public void shouldUpdateAccountRequestDataForRejectedRequest() throws IOException, URISyntaxException, InvalidRequestException {

        UpdateAccountRequestInputData accountInputData = new UpdateAccountRequestInputData();
        accountInputData.setStatus("Rejected");
        String testRequestId = "TestRequestId";
        String testClientId = "TestClientId";
        String testClientRole = "CUSTOMER";
        String txnCorrelationId = "correlationId";
        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        t.setStatusUpdatedDateTime();
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenReturn(accountRequest);
        when(accountRequestDAO.updateAccountRequest(accountRequest, InternalUserRoleEnum.CUSTOMER)).thenReturn(t);
        when(stateChangeMachine.getUpdatableStatus(anyString(), anyString())).thenReturn(AccountRequestStatusEnum.REJECTED);
        UpdateAccountRequestOutputData updateAccountRequestOutputData = accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, testClientRole, txnCorrelationId);
        assertNotNull(updateAccountRequestOutputData);
    }

    @Test(expected = InvalidRequestException.class)
    public void throwsExceptionIfTheStatusIsntAuthorisedOrRejected() throws IOException, URISyntaxException, InvalidRequestException {
        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        UpdateAccountRequestInputData accountInputData = new UpdateAccountRequestInputData();
        accountInputData.setStatus("AwaitingAuthorisation");
        String testRequestId = "TestRequestId";
        String testClientId = "TestClientId";
        String txnCorrelationId = "correlationId";
        String testClientRole = "CUSTOMER";
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenReturn(accountRequest);
        UpdateAccountRequestOutputData updateAccountRequestOutputData = accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, testClientRole, txnCorrelationId);
    }


    @Test
    public void shouldUpdateAccountRequestAsRevokedForValidRequestAuthorised() throws IOException, URISyntaxException {
        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        long entitlementId = 1122L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        accountRequest.setAccountRequestStatus(AccountRequestStatusEnum.AUTHORISED);
        accountRequest.setEntitlementId(entitlementId);
        String testRequestId = "testRequestid";
        String txnCorrelationId = "correlationId";
        String testClientRole = "CUSTOMER";
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        when(stateChangeMachine.getUpdatableStatus(AccountRequestDataConstant.AUTHORISED, AccountRequestStatusEnum.REVOKED)).thenReturn(AccountRequestStatusEnum.REVOKED);
        String correlationId = "correlationId";
        String internalUserId = "SYSTEM";
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenReturn(accountRequest);
        when(accountRequestDAO.updateAccountRequest(accountRequest, InternalUserRoleEnum.CUSTOMER)).thenReturn(t);
        accountRequestDataService.revokeAccountRequestData(testRequestId, testClientRole, correlationId);
        assertTrue(true);
    }

    @Test
    public void shouldUpdateAccountRequestAsRevokedForValidRequestAwaitingAuth() throws IOException, URISyntaxException {
        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        accountRequest.setAccountRequestStatus(AccountRequestStatusEnum.AWAITINGAUTHORISATION);
        String testRequestId = "testRequestid";
        String testClientRole = "CUSTOMER";
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        when(stateChangeMachine.getUpdatableStatus(AccountRequestDataConstant.AWAITING_AUTHORISATION, AccountRequestStatusEnum.REVOKED)).thenReturn(AccountRequestStatusEnum.REVOKED);
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenReturn(accountRequest);
        when(accountRequestDAO.updateAccountRequest(accountRequest, InternalUserRoleEnum.CUSTOMER)).thenReturn(t);
        String correlationId = "correlationId";
        String internalUserId = "SYSTEM";
        accountRequestDataService.revokeAccountRequestData(testRequestId, testClientRole, correlationId);
        assertTrue(true);
    }

    @Test(expected = InvalidRequestException.class)
    public void shouldThrowExceptionWhenRevokeRequestForAlreadyRevokedARD() throws IOException, URISyntaxException {
        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        long entitlementId = 1122L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        accountRequest.setAccountRequestStatus(AccountRequestStatusEnum.REVOKED);
        accountRequest.setEntitlementId(entitlementId);
        String testRequestId = "testRequestid";
        String testClientRole = "CUSTOMER";
        when(stateChangeMachine.getUpdatableStatus(AccountRequestDataConstant.REVOKED, AccountRequestStatusEnum.REVOKED)).thenThrow(new InvalidRequestException(BAD_REQUEST_INVALID_REQUEST, ARD_API_ERR_007));
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenReturn(accountRequest);
        String correlationId = "correlationId";
        String internalUserId = "SYSTEM";
        accountRequestDataService.revokeAccountRequestData(testRequestId, testClientRole, correlationId);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowRecordNotFoundExceptionIfNoRecordReturnedForGETWithPath() throws IOException {
        String testRequestId = "test";
        String txnCorrelationId = "correlationId";
        when(accountRequestDAO.findAccountRequest(testRequestId)).thenThrow(new RecordNotFoundException("not found"));
        accountRequestDataService.findByAccountRequestExternalIdentifier(testRequestId, txnCorrelationId);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowRecordNotFoundExceptionIfNoRecordReturnedForGETWithQuery() throws IOException {
        String testRequestId = "test";
        String clientid = "clientid";
        String txnCorrelationId = "correlationId";
        when(accountRequestDAO.findAccountRequest(testRequestId, clientid)).thenThrow(new RecordNotFoundException("not found"));
        accountRequestDataService.findByAccountRequestExternalIdentifierAndProviderClientId(testRequestId, clientid, txnCorrelationId);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowRecordNotFoundExceptionIfNoRecordReturnedForUpdateRequest() throws IOException, URISyntaxException {
        String testRequestId = "test";
        UpdateAccountRequestInputData accountInputData = new UpdateAccountRequestInputData();
        accountInputData.setStatus("Authorised");
        String txnCorrelationId = "correlationId";
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenThrow(new RecordNotFoundException("not found"));
        accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, "some-role", txnCorrelationId);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowRecordNotFoundExceptionIfNoRecordReturnedForDeleteRequest() throws IOException, URISyntaxException {
        String testRequestId = "test";
        UpdateAccountRequestInputData accountInputData = new UpdateAccountRequestInputData();
        accountInputData.setStatus("Authorised");
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenThrow(new RecordNotFoundException("not found"));
        accountRequestDataService.revokeAccountRequestData(testRequestId, "some-role", "correlation-id");
    }

}
