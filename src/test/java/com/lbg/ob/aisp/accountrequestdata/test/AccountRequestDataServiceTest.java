package com.lbg.ob.aisp.accountrequestdata.test;

import com.lbg.ob.aisp.accountrequestdata.data.AccountRequest;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestStatusEnum;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestStatusHistory;
import com.lbg.ob.aisp.accountrequestdata.data.CreateAccountInputRequest;
import com.lbg.ob.aisp.accountrequestdata.data.InternalUserRoleEnum;
import com.lbg.ob.aisp.accountrequestdata.data.UpdateAccountRequestInputData;
import com.lbg.ob.aisp.accountrequestdata.data.UpdateAccountRequestOutputData;
import com.lbg.ob.aisp.accountrequestdata.exception.InvalidRequestException;
import com.lbg.ob.aisp.accountrequestdata.exception.RecordNotFoundException;
import com.lbg.ob.aisp.accountrequestdata.exception.ResourceAccessException;
import com.lbg.ob.aisp.accountrequestdata.service.AccountRequestDAO;
import com.lbg.ob.aisp.accountrequestdata.service.AccountRequestDataServiceImpl;
import com.lbg.ob.aisp.accountrequestdata.service.EntitlementProxyService;
import com.lbg.ob.aisp.accountrequestdata.util.AccountRequestDataConstant;
import com.lbg.ob.aisp.accountrequestdata.util.StateChangeMachine;
import com.netflix.hystrix.exception.HystrixTimeoutException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import static com.lbg.ob.aisp.accountrequestdata.exception.ExceptionConstants.ARD_API_ERR_007;
import static com.lbg.ob.aisp.accountrequestdata.exception.ExceptionConstants.BAD_REQUEST_INVALID_REQUEST;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RequestContextHolder.class, ServletRequestAttributes.class})
public class AccountRequestDataServiceTest {

    @Mock
    private static final Logger logger = LogManager.getLogger(AccountRequestDataServiceTest.class);
    
    @Mock
    AccountRequestDAO accountRequestDAO;

    @Mock
    EntitlementProxyService entitlementService;

    @Mock
    StateChangeMachine stateChangeMachine;

    @InjectMocks
    AccountRequestDataServiceImpl accountRequestDataService;

    private String clientId = "ace";

    private Boolean fovIndicator = true;

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
        String entitlementAccessCode = "1234-rqws";
        accountInputData.setEntitlementAccessCode(entitlementAccessCode);
        long accountRequestIdentifier = 1234L;
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        t.setStatusUpdatedDateTime();
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenReturn(accountRequest);
        when(accountRequestDAO.updateAccountRequest(accountRequest, InternalUserRoleEnum.CUSTOMER)).thenReturn(t);
        when(stateChangeMachine.getUpdatableStatus(anyString(), anyString())).thenReturn(AccountRequestStatusEnum.AUTHORISED);
        UpdateAccountRequestOutputData updateAccountRequestOutputData = accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, testClientRole);
        assertNotNull(updateAccountRequestOutputData);

    }

    @Test(expected = InvalidRequestException.class)
    public void shouldThrowExceptionWhenUpdateForAuthorisedWithoutEntitlementId() throws IOException, URISyntaxException, InvalidRequestException {
        UpdateAccountRequestInputData accountInputData = new UpdateAccountRequestInputData();
        accountInputData.setStatus("Authorised");
        String testRequestId = "TestRequestId";
        String testClientRole = "CUSTOMER";

        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenReturn(accountRequest);
        when(stateChangeMachine.getUpdatableStatus(anyString(), anyString())).thenReturn(AccountRequestStatusEnum.AUTHORISED);
        UpdateAccountRequestOutputData updateAccountRequestOutputData = accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, testClientRole);
        assertNotNull(updateAccountRequestOutputData);

    }
    
    @Test(expected = InvalidRequestException.class)
    public void shouldThrowExceptionWhenUpdateForAuthorisedWithoutEntitlementAccessCode() throws IOException, URISyntaxException, InvalidRequestException {
        UpdateAccountRequestInputData accountInputData = new UpdateAccountRequestInputData();
        accountInputData.setStatus("Authorised");
        String testRequestId = "TestRequestId";
        String testClientRole = "CUSTOMER";
        accountInputData.setEntitlementId(1234L);

        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenReturn(accountRequest);
        when(stateChangeMachine.getUpdatableStatus(anyString(), anyString())).thenReturn(AccountRequestStatusEnum.AUTHORISED);
        UpdateAccountRequestOutputData updateAccountRequestOutputData = accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, testClientRole);
        assertNotNull(updateAccountRequestOutputData);

    }

    @Test
    public void shouldUpdateAccountRequestDataForRejectedRequest() throws IOException, URISyntaxException, InvalidRequestException {

        UpdateAccountRequestInputData accountInputData = new UpdateAccountRequestInputData();
        accountInputData.setStatus("Rejected");
        String testRequestId = "TestRequestId";
        String testClientRole = "CUSTOMER";
        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        t.setStatusUpdatedDateTime();
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenReturn(accountRequest);
        when(accountRequestDAO.updateAccountRequest(accountRequest, InternalUserRoleEnum.CUSTOMER)).thenReturn(t);
        when(stateChangeMachine.getUpdatableStatus(anyString(), anyString())).thenReturn(AccountRequestStatusEnum.REJECTED);
        UpdateAccountRequestOutputData updateAccountRequestOutputData = accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, testClientRole);
        assertNotNull(updateAccountRequestOutputData);
    }
    
    @Test
    public void shouldUpdateAccountRequestDataForRevokedRequest() throws IOException, URISyntaxException, InvalidRequestException {

        UpdateAccountRequestInputData accountInputData = new UpdateAccountRequestInputData();
        accountInputData.setStatus("Revoked");
        String testRequestId = "TestRequestId";
        String testClientRole = "CUSTOMER";
        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        t.setStatusUpdatedDateTime();
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenReturn(accountRequest);
        when(accountRequestDAO.updateAccountRequest(accountRequest, InternalUserRoleEnum.CUSTOMER)).thenReturn(t);
        when(stateChangeMachine.getUpdatableStatus(anyString(), anyString())).thenReturn(AccountRequestStatusEnum.REVOKED);
        UpdateAccountRequestOutputData updateAccountRequestOutputData = accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, testClientRole);
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
        String testClientRole = "CUSTOMER";
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenReturn(accountRequest);
        UpdateAccountRequestOutputData updateAccountRequestOutputData = accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, testClientRole);
    }


    @Test
    public void shouldUpdateAccountRequestAsRevokedForValidRequestAuthorised() throws IOException, URISyntaxException, ExecutionException, InterruptedException {
        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        long entitlementId = 1122L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        accountRequest.setAccountRequestStatus(AccountRequestStatusEnum.AUTHORISED);
        accountRequest.setEntitlementId(entitlementId);
        String testRequestId = "testRequestid";
        String testClientRole = "CUSTOMER";
        HttpHeaders headers = new HttpHeaders();
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        when(stateChangeMachine.getUpdatableStatus(AccountRequestDataConstant.AUTHORISED, AccountRequestStatusEnum.REVOKED)).thenReturn(AccountRequestStatusEnum.REVOKED);
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenReturn(accountRequest);
        when(accountRequestDAO.updateAccountRequest(accountRequest, InternalUserRoleEnum.CUSTOMER)).thenReturn(t);
        accountRequestDataService.revokeAccountRequestData(testRequestId, testClientRole, clientId, fovIndicator,headers);
        assertTrue(true);
    }

    @Test
    public void shouldUpdateAccountRequestAsRevokedForValidRequestAwaitingAuth() throws IOException, URISyntaxException, ExecutionException, InterruptedException {
        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        accountRequest.setAccountRequestStatus(AccountRequestStatusEnum.AWAITINGAUTHORISATION);
        String testRequestId = "testRequestid";
        String testClientRole = "CUSTOMER";
        HttpHeaders headers = new HttpHeaders();
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        when(stateChangeMachine.getUpdatableStatus(AccountRequestDataConstant.AWAITING_AUTHORISATION, AccountRequestStatusEnum.REVOKED)).thenReturn(AccountRequestStatusEnum.REVOKED);
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenReturn(accountRequest);
        when(accountRequestDAO.updateAccountRequest(accountRequest, InternalUserRoleEnum.CUSTOMER)).thenReturn(t);
        accountRequestDataService.revokeAccountRequestData(testRequestId, testClientRole, clientId, fovIndicator, headers);
        assertTrue(true);
    }

    @Test(expected = InvalidRequestException.class)
    public void shouldThrowExceptionWhenRevokeRequestForAlreadyRevokedARD() throws IOException, URISyntaxException, ExecutionException, InterruptedException {
        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        long entitlementId = 1122L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        accountRequest.setAccountRequestStatus(AccountRequestStatusEnum.REVOKED);
        accountRequest.setEntitlementId(entitlementId);
        String testRequestId = "testRequestid";
        String testClientRole = "CUSTOMER";
        HttpHeaders headers = new HttpHeaders();
        when(stateChangeMachine.getUpdatableStatus(AccountRequestDataConstant.REVOKED, AccountRequestStatusEnum.REVOKED)).thenThrow(new InvalidRequestException(BAD_REQUEST_INVALID_REQUEST, ARD_API_ERR_007));
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenReturn(accountRequest);
        accountRequestDataService.revokeAccountRequestData(testRequestId, testClientRole, clientId, fovIndicator, headers);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowRecordNotFoundExceptionIfNoRecordReturnedForGETWithPath() throws IOException {
        String testRequestId = "test";
        when(accountRequestDAO.findAccountRequest(testRequestId)).thenThrow(new RecordNotFoundException("not found"));
        accountRequestDataService.findByAccountRequestExternalIdentifier(testRequestId);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowRecordNotFoundExceptionIfNoRecordReturnedForGETWithQuery() throws IOException {
        String testRequestId = "test";
        String clientid = "clientid";
        when(accountRequestDAO.findAccountRequest(testRequestId, clientid)).thenThrow(new RecordNotFoundException("not found"));
        accountRequestDataService.findByAccountRequestExternalIdentifierAndProviderClientId(testRequestId, clientid);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowRecordNotFoundExceptionIfNoRecordReturnedForUpdateRequest() throws IOException, URISyntaxException {
        String testRequestId = "test";
        UpdateAccountRequestInputData accountInputData = new UpdateAccountRequestInputData();
        accountInputData.setStatus("Authorised");
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenThrow(new RecordNotFoundException("not found"));
        accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, "some-role");
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowRecordNotFoundExceptionIfNoRecordReturnedForDeleteRequest() throws IOException, URISyntaxException, ExecutionException, InterruptedException {
        String testRequestId = "test";
        UpdateAccountRequestInputData accountInputData = new UpdateAccountRequestInputData();
        accountInputData.setStatus("Authorised");
        HttpHeaders headers = new HttpHeaders();
        when(accountRequestDAO.getAccountRequest(testRequestId)).thenThrow(new RecordNotFoundException("not found"));
        accountRequestDataService.revokeAccountRequestData(testRequestId, "some-role", clientId, fovIndicator, headers);
    }

    @Test(expected = ResourceAccessException.class)
    public void shouldThrowResourceAccessExceptionForCreateFallback() throws Exception {
        String clientId = "clientid";
        String fapiFinancialId = "fapiid";
        Throwable ex = new HystrixTimeoutException();
        CreateAccountInputRequest createAccountInputRequest = null;
//        Mockito.doNothing().when(LOGGER).logException(anyString(), any(Throwable.class));
        Whitebox.invokeMethod(accountRequestDataService,"fallbackCreate",createAccountInputRequest, clientId, fapiFinancialId, ex);
    }

    @Test(expected = ResourceAccessException.class)
    public void shouldThrowResourceAccessExceptionForFallbackFindWithClientId() throws Exception {
        String clientId = "clientid";
        String accountRequestId = "accountRequestId";
        Throwable ex = new HystrixTimeoutException();
        Mockito.doNothing().when(logger).error(anyString(), any(Throwable.class));
        Whitebox.invokeMethod(accountRequestDataService,"fallbackFindWithClientId", accountRequestId, clientId, ex);
    }

    @Test(expected = ResourceAccessException.class)
    public void shouldThrowResourceAccessExceptionForFallbackFind() throws Exception {
        String accountRequestId = "accountRequestId";
        String txnCorrelationId = "txnid";
        Throwable ex = new HystrixTimeoutException();
//        Mockito.doNothing().when(LOGGER).logException(anyString(), any(Throwable.class));
        Whitebox.invokeMethod(accountRequestDataService,"fallbackFind", accountRequestId, ex);
    }

    @Test(expected = ResourceAccessException.class)
    public void shouldThrowResourceAccessExceptionForFallbackRevoke() throws Exception {
        String accountRequestId = "accountRequestId";
        String txnCorrelationId = "txnid";
        HttpHeaders httpheaders = new HttpHeaders();
        		
        Throwable ex = new HystrixTimeoutException();
//        Mockito.doNothing().when(LOGGER).logException(anyString(), any(Throwable.class));
        Whitebox.invokeMethod(accountRequestDataService,"fallbackRevoke", accountRequestId, "clientRole", clientId, false, httpheaders.toSingleValueMap(),ex);
    }

    @Test(expected = ResourceAccessException.class)
    public void shouldThrowResourceAccessExceptionForFallbackUpdate() throws Exception {
        String accountRequestId = "accountRequestId";
        String txnCorrelationId = "txnid";
        Throwable ex = new HystrixTimeoutException();

//        Mockito.doNothing().when(LOGGER).logException(anyString(), any(Throwable.class));
        UpdateAccountRequestInputData accountInputData = null;
        Whitebox.invokeMethod(accountRequestDataService,"fallbackUpdate", accountInputData, accountRequestId, "clientRole", ex);
    }

}
