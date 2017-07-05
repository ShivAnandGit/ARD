package com.lbg.aaf.entitlement.entitlementaccountrequestdata.test;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.*;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.InvalidRequestException;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.RecordNotFoundException;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository.AccountRequestInfoRepository;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository.AccountRequestStatusChangeHistoryRepository;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.service.AccountRequestDataServiceImpl;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.service.EntitlementService;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.AccountRequestDataConstant;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.StateChangeMachine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.ExceptionConstants.ARD_API_ERR_007;
import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.ExceptionConstants.BAD_REQUEST_INVALID_REQUEST;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class AccountRequestDataServiceTest {
    @Mock
    AccountRequestInfoRepository accountRequestInfoRepository;

    @Mock
    AccountRequestStatusChangeHistoryRepository accountRequestInfoHistoryRepository;

    @Mock
    EntitlementService entitlementService;

    @Mock
    StateChangeMachine stateChangeMachine;

    @InjectMocks
    AccountRequestDataServiceImpl accountRequestDataService;

    @Before
    public void init() {
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
        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        when(accountRequestInfoRepository.findByAccountRequestExternalIdentifier(testRequestId)).thenReturn(accountRequest);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        when(accountRequestInfoHistoryRepository.save(any(AccountRequestStatusHistory.class))).thenReturn(t);
        when(stateChangeMachine.getUpdatableStatus(anyString(), anyString())).thenReturn(AccountRequestStatusEnum.AUTHORISED);
        when(accountRequestInfoRepository.save(accountRequest)).thenReturn(accountRequest);
        UpdateAccountRequestOutputData updateAccountRequestOutputData = accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, testClientRole);
        assertNotNull(updateAccountRequestOutputData);

    }

    @Test(expected = InvalidRequestException.class)
    public void shouldThrowExceptionWhenUpdateForAuthorisedWithoutEntitlementId() throws IOException, URISyntaxException, InvalidRequestException {
        UpdateAccountRequestInputData accountInputData = new UpdateAccountRequestInputData();
        accountInputData.setStatus("Authorised");
        String testRequestId = "TestRequestId";
        String testClientId = "TestClientId";
        String testClientRole = "CUSTOMER";
        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        when(accountRequestInfoRepository.findByAccountRequestExternalIdentifier(testRequestId)).thenReturn(accountRequest);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        when(accountRequestInfoHistoryRepository.save(any(AccountRequestStatusHistory.class))).thenReturn(t);
        when(stateChangeMachine.getUpdatableStatus(anyString(), anyString())).thenReturn(AccountRequestStatusEnum.AUTHORISED);
        when(accountRequestInfoRepository.save(accountRequest)).thenReturn(accountRequest);
        UpdateAccountRequestOutputData updateAccountRequestOutputData = accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, testClientRole);
        assertNotNull(updateAccountRequestOutputData);

    }

    @Test
    public void shouldUpdateAccountRequestDataForRejectedRequest() throws IOException, URISyntaxException, InvalidRequestException {

        UpdateAccountRequestInputData accountInputData = new UpdateAccountRequestInputData();
        accountInputData.setStatus("Rejected");
        String testRequestId = "TestRequestId";
        String testClientId = "TestClientId";
        String testClientRole = "CUSTOMER";
        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        when(accountRequestInfoRepository.findByAccountRequestExternalIdentifier(testRequestId)).thenReturn(accountRequest);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        when(stateChangeMachine.getUpdatableStatus(anyString(), anyString())).thenReturn(AccountRequestStatusEnum.REJECTED);
        when(accountRequestInfoHistoryRepository.save(any(AccountRequestStatusHistory.class))).thenReturn(t);
        when(accountRequestInfoRepository.save(accountRequest)).thenReturn(accountRequest);
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
        String testClientId = "TestClientId";
        String testClientRole = "CUSTOMER";
        when(accountRequestInfoRepository.findByAccountRequestExternalIdentifier(testRequestId)).thenReturn(accountRequest);
        UpdateAccountRequestOutputData updateAccountRequestOutputData = accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, testClientRole);
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
        String testClientRole = "CUSTOMER";
        when(accountRequestInfoRepository.findByAccountRequestExternalIdentifier(testRequestId)).thenReturn(accountRequest);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        when(accountRequestInfoHistoryRepository.save(any(AccountRequestStatusHistory.class))).thenReturn(t);
        when(stateChangeMachine.getUpdatableStatus(AccountRequestDataConstant.AUTHORISED, AccountRequestStatusEnum.REVOKED)).thenReturn(AccountRequestStatusEnum.REVOKED);
        when(accountRequestInfoRepository.save(accountRequest)).thenReturn(accountRequest);
        String correlationId = "correlationId";
        String internalUserId = "SYSTEM";
//        doNothing().when(entitlementService.revokeEntitlement(entitlementId, internalUserId, testClientRole, correlationId));
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
        when(accountRequestInfoRepository.findByAccountRequestExternalIdentifier(testRequestId)).thenReturn(accountRequest);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        when(accountRequestInfoHistoryRepository.save(any(AccountRequestStatusHistory.class))).thenReturn(t);
        when(stateChangeMachine.getUpdatableStatus(AccountRequestDataConstant.AWAITING_AUTHORISATION, AccountRequestStatusEnum.REVOKED)).thenReturn(AccountRequestStatusEnum.REVOKED);
        when(accountRequestInfoRepository.save(accountRequest)).thenReturn(accountRequest);
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
        when(accountRequestInfoRepository.findByAccountRequestExternalIdentifier(testRequestId)).thenReturn(accountRequest);
        when(stateChangeMachine.getUpdatableStatus(AccountRequestDataConstant.REVOKED, AccountRequestStatusEnum.REVOKED)).thenThrow(new InvalidRequestException(BAD_REQUEST_INVALID_REQUEST, ARD_API_ERR_007));
        String correlationId = "correlationId";
        String internalUserId = "SYSTEM";
        accountRequestDataService.revokeAccountRequestData(testRequestId, testClientRole, correlationId);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowRecordNotFoundExceptionIfNoRecordReturnedForGETWithPath() throws IOException {
        String testRequestId = "test";
        when(accountRequestInfoRepository.findByAccountRequestExternalIdentifier(testRequestId)).thenReturn(null);
        accountRequestDataService.findByAccountRequestExternalIdentifier(testRequestId);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowRecordNotFoundExceptionIfNoRecordReturnedForGETWithQuery() throws IOException {
        String testRequestId = "test";
        String clientid = "clientid";
        when(accountRequestInfoRepository.findByAccountRequestExternalIdentifierAndProviderClientIdAndAccountRequestStatus(testRequestId, clientid, "AwaitingAuthorisation")).thenReturn(null);
        accountRequestDataService.findByAccountRequestExternalIdentifierAndProviderClientId(testRequestId, clientid);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowRecordNotFoundExceptionIfNoRecordReturnedForUpdateRequest() throws IOException, URISyntaxException {
        String testRequestId = "test";
        when(accountRequestInfoRepository.findByAccountRequestExternalIdentifier(testRequestId)).thenReturn(null);
        UpdateAccountRequestInputData accountInputData = new UpdateAccountRequestInputData();
        accountInputData.setStatus("Authorised");
        accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, "some-role");
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowRecordNotFoundExceptionIfNoRecordReturnedForDeleteRequest() throws IOException, URISyntaxException {
        String testRequestId = "test";
        when(accountRequestInfoRepository.findByAccountRequestExternalIdentifier(testRequestId)).thenReturn(null);
        UpdateAccountRequestInputData accountInputData = new UpdateAccountRequestInputData();
        accountInputData.setStatus("Authorised");
        accountRequestDataService.revokeAccountRequestData(testRequestId, "some-role", "correlation-id");
    }

}
