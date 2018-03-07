package com.lbg.ob.aisp.accountrequestdata.test;

import com.lbg.ob.aisp.accountrequestdata.data.AccountRequest;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestOutputResponse;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestStatusEnum;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestStatusHistory;
import com.lbg.ob.aisp.accountrequestdata.data.CreateAccountInputData;
import com.lbg.ob.aisp.accountrequestdata.data.InternalUserRoleEnum;
import com.lbg.ob.aisp.accountrequestdata.exception.InvalidRequestException;
import com.lbg.ob.aisp.accountrequestdata.exception.RecordNotFoundException;
import com.lbg.ob.aisp.accountrequestdata.repository.AccountRequestInfoRepository;
import com.lbg.ob.aisp.accountrequestdata.repository.AccountRequestStatusChangeHistoryRepository;
import com.lbg.ob.aisp.accountrequestdata.repository.ProviderPermissionsRepository;
import com.lbg.ob.aisp.accountrequestdata.service.AccountRequestDAOImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by pbabb1 on 7/19/17.
 */
public class AccountRequestDAOTest {
    @Mock
    AccountRequestInfoRepository accountRequestInfoRepository;

    @Mock
    AccountRequestStatusChangeHistoryRepository accountRequestInfoHistoryRepository;

    @Mock
    ProviderPermissionsRepository providerPermissionsRepository;

    @InjectMocks
    AccountRequestDAOImpl accountRequestDAO;

    private String json = "{\"Data\":{\"Permissions\":[\"ReadBeneficiariesDetail\",\"ReadDirectDebits\"],\"TransactionToDateTime\":\"2017-06-01T09:24:30.975Z\",\"ExpirationDateTime\":\"2017-06-01T09:24:30.975Z\",\"TransactionFromDateTime\":\"2017-06-01T09:24:30.975Z\"},\"Risk\":{}}";
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldUpdateAccountRequestData() throws IOException, URISyntaxException, InvalidRequestException {
        long accountRequestIdentifier = 1234L;
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAccountRequestIdentifier(accountRequestIdentifier);
        accountRequest.setAccountRequestStatus(AccountRequestStatusEnum.AUTHORISED);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        when(accountRequestInfoHistoryRepository.save(any(AccountRequestStatusHistory.class))).thenReturn(t);
        when(accountRequestInfoRepository.save(accountRequest)).thenReturn(accountRequest);
        AccountRequestStatusHistory accountRequestStatusHistory = accountRequestDAO.updateAccountRequest(accountRequest, InternalUserRoleEnum.CUSTOMER);
        assertNotNull(accountRequestStatusHistory);
    }

    @Test
    public void shouldCreateAccountRequest() throws IOException, InterruptedException {
        AccountRequest accountRequestInfo = new AccountRequest();
        AccountRequest accountRequestInfo2 = new AccountRequest(new CreateAccountInputData(), "", "", json);
        accountRequestInfo2.setPermissions(new ArrayList<>());
        String accountRequestExternalIdentifier = "1234";
        accountRequestInfo2.setAccountRequestExternalIdentifier(accountRequestExternalIdentifier);
        when(accountRequestInfoRepository.save(accountRequestInfo)).thenReturn(accountRequestInfo2);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        when(accountRequestInfoHistoryRepository.save(any(AccountRequestStatusHistory.class))).thenReturn(t);
        when(providerPermissionsRepository.findByCode(anyString())).thenReturn(new ArrayList<>());
        AccountRequestOutputResponse accountRequestOutput = accountRequestDAO.createAccountRequest(accountRequestInfo);
        assertEquals(accountRequestExternalIdentifier, accountRequestOutput.getAccountRequestOutputData().getAccountRequestExternalIdentifier());
    }

    @Test
    public void shouldRevokeAccountRequest() {
        AccountRequest accountRequestInfo = new AccountRequest();
        when(accountRequestInfoRepository.save(accountRequestInfo)).thenReturn(accountRequestInfo);
        AccountRequestStatusHistory t = new AccountRequestStatusHistory();
        when(accountRequestInfoHistoryRepository.save(any(AccountRequestStatusHistory.class))).thenReturn(t);
        accountRequestDAO.revokeAccountRequest(InternalUserRoleEnum.CUSTOMER.name(), accountRequestInfo, AccountRequestStatusEnum.REVOKED);
        //if we can reach the final line, then the test passes
        assertTrue(true);
    }

    @Test
    public void shouldFindAccountRequestWithExternalId() throws IOException {
        AccountRequest accountRequestInfo = new AccountRequest(new CreateAccountInputData(), "", "", json);
        String accountRequestExternalIdentifier = accountRequestInfo.getAccountRequestExternalIdentifier();
        when(accountRequestInfoRepository.findByAccountRequestExternalIdentifier(accountRequestExternalIdentifier)).thenReturn(accountRequestInfo);
        when(providerPermissionsRepository.findByCode(anyString())).thenReturn(new ArrayList<>());
        AccountRequestOutputResponse accountRequest = accountRequestDAO.findAccountRequest(accountRequestExternalIdentifier);
        assertEquals(accountRequestExternalIdentifier, accountRequest.getAccountRequestOutputData().getAccountRequestExternalIdentifier());
    }

    @Test
    public void shouldFindAccountRequestWithIdAndClient() throws IOException {
        AccountRequest accountRequestInfo = new AccountRequest(new CreateAccountInputData(), "", "", json);
        String accountRequestExternalIdentifier = accountRequestInfo.getAccountRequestExternalIdentifier();
        String status = AccountRequestStatusEnum.AWAITINGAUTHORISATION.getValue();
        String clientId = "client";
        when(accountRequestInfoRepository.findByAccountRequestExternalIdentifierAndProviderClientIdAndAccountRequestStatus(accountRequestExternalIdentifier, clientId)).thenReturn(accountRequestInfo);
        when(providerPermissionsRepository.findByCode(anyString())).thenReturn(new ArrayList<>());
        AccountRequestOutputResponse accountRequest = accountRequestDAO.findAccountRequest(accountRequestExternalIdentifier, clientId);
        assertEquals(accountRequestExternalIdentifier, accountRequest.getAccountRequestOutputData().getAccountRequestExternalIdentifier());
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenRecordNotFoundWithRequestWithIdAndClient() throws IOException {
        AccountRequest accountRequestInfo = new AccountRequest(new CreateAccountInputData(), "", "", json);
        String accountRequestExternalIdentifier = accountRequestInfo.getAccountRequestExternalIdentifier();
        String clientId = "client";
        when(accountRequestInfoRepository.findByAccountRequestExternalIdentifierAndProviderClientIdAndAccountRequestStatus(accountRequestExternalIdentifier, clientId)).thenReturn(null);
        AccountRequestOutputResponse accountRequest = accountRequestDAO.findAccountRequest(accountRequestExternalIdentifier, clientId);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenRecordNotFoundWithExternalId() throws IOException {
        AccountRequest accountRequestInfo = new AccountRequest(new CreateAccountInputData(), "", "", json);
        String accountRequestExternalIdentifier = accountRequestInfo.getAccountRequestExternalIdentifier();
        when(accountRequestInfoRepository.findByAccountRequestExternalIdentifier(accountRequestExternalIdentifier)).thenReturn(null);
        AccountRequestOutputResponse accountRequest = accountRequestDAO.findAccountRequest(accountRequestExternalIdentifier);
    }
}
