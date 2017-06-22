package com.lbg.aaf.entitlement.entitlementaccountrequestdata.test;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.*;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository.AccountRequestAssociatedAccountRepository;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository.AccountRequestInfoRepository;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository.AccountRequestStatusChangeHistoryRepository;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.service.AccountRequestDataServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class AccountRequestDataServiceTest {
    @Mock
    AccountRequestInfoRepository accountRequestInfoRepository;

    @Mock
    AccountRequestStatusChangeHistoryRepository accountRequestInfoHistoryRepository;

    @Mock
    AccountRequestAssociatedAccountRepository associatedAccountsResourceRepository;

    @InjectMocks
    AccountRequestDataServiceImpl accountRequestDataService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldUpdateAccountRequestDataForAuthorisedRequest() throws IOException, URISyntaxException {

        UpdateAccountInputData accountInputData = new UpdateAccountInputData();
        accountInputData.setAccountIds(Arrays.asList("1","2","3"));
        accountInputData.setStatus("Authorised");
        String testRequestId = "TestRequestId";
        String testClientId = "TestClientId";
        String testClientRole = "CUSTOMER";
        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        when(accountRequestInfoRepository.findByAccountRequestExternalIdentifier(testRequestId)).thenReturn(accountRequest);
        when(associatedAccountsResourceRepository.save(any(AccountRequestAssociatedAccountResource.class))).thenReturn(null);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        when(accountRequestInfoHistoryRepository.save(any(AccountRequestStatusHistory.class))).thenReturn(t);
        when(accountRequestInfoRepository.save(accountRequest)).thenReturn(accountRequest);
        UpdateRequestOutputData updateRequestOutputData = accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, testClientId, testClientRole);
        assertNotNull(updateRequestOutputData);

    }

    @Test
    public void shouldUpdateAccountRequestDataForRejectedRequest() throws IOException, URISyntaxException {

        UpdateAccountInputData accountInputData = new UpdateAccountInputData();
        accountInputData.setAccountIds(Arrays.asList("1","2","3"));
        accountInputData.setStatus("Rejected");
        String testRequestId = "TestRequestId";
        String testClientId = "TestClientId";
        String testClientRole = "CUSTOMER";
        AccountRequest accountRequest = new AccountRequest();
        long accountRequestIdentifier = 1234L;
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        when(accountRequestInfoRepository.findByAccountRequestExternalIdentifier(testRequestId)).thenReturn(accountRequest);
        when(associatedAccountsResourceRepository.save(any(AccountRequestAssociatedAccountResource.class))).thenReturn(null);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        when(accountRequestInfoHistoryRepository.save(any(AccountRequestStatusHistory.class))).thenReturn(t);
        when(accountRequestInfoRepository.save(accountRequest)).thenReturn(accountRequest);
        UpdateRequestOutputData updateRequestOutputData = accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, testClientId, testClientRole);
        assertNotNull(updateRequestOutputData);
    }

    @Test(expected = RuntimeException.class)
    public void throwsExceptionIfTheStatusIsntAuthorised() throws IOException, URISyntaxException {
        UpdateAccountInputData accountInputData = new UpdateAccountInputData();
        accountInputData.setAccountIds(Arrays.asList("1","2","3"));
        accountInputData.setStatus("AwaitingAuthorisation");
        String testRequestId = "TestRequestId";
        String testClientId = "TestClientId";
        String testClientRole = "CUSTOMER";
        UpdateRequestOutputData updateRequestOutputData = accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, testClientId, testClientRole);
    }

    @Test(expected = RuntimeException.class)
    public void throwsExceptionIfTheStatusIsntRecognized() throws IOException, URISyntaxException {
        UpdateAccountInputData accountInputData = new UpdateAccountInputData();
        accountInputData.setAccountIds(Arrays.asList("1","2","3"));
        accountInputData.setStatus("SOMESTATUS");
        String testRequestId = "TestRequestId";
        String testClientId = "TestClientId";
        String testClientRole = "CUSTOMER";
        UpdateRequestOutputData updateRequestOutputData = accountRequestDataService.updateAccountRequestData(accountInputData, testRequestId, testClientId, testClientRole);
    }
}
