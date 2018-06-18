package com.lbg.ob.aisp.accountrequestdata.test;

import com.lbg.ob.aisp.accountrequestdata.AccountRequestDataServiceApplication;
import com.lbg.ob.aisp.accountrequestdata.controller.AccountRequestDataController;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestOutputData;
import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestStatusHistory;
import com.lbg.ob.aisp.accountrequestdata.data.CreateAccountInputData;
import com.lbg.ob.aisp.accountrequestdata.data.CreateAccountInputRequest;
import com.lbg.ob.aisp.accountrequestdata.data.PermissionsEnum;
import com.lbg.ob.aisp.accountrequestdata.data.ProviderPermission;
import com.lbg.ob.aisp.accountrequestdata.data.ServicerSchemeNameEnum;
import com.lbg.ob.aisp.accountrequestdata.data.UpdateAccountRequestInputData;
import com.lbg.ob.aisp.accountrequestdata.data.UpdateAccountRequestOutputData;
import com.lbg.ob.aisp.accountrequestdata.service.AccountRequestDataService;
import com.lbg.ob.aisp.accountrequestdata.service.AccountRequestDataServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


//@RunWith(SpringRunner.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = {AccountRequestDataServiceApplication.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@EnableWebMvc
@PrepareForTest({AccountRequestDataController.class, AccountRequestDataServiceImpl.class, AccountRequestDataService.class, AccountRequestDataServiceApplication.class})
@PowerMockIgnore({"javax.management.*"})
public class AccountRequestDataTest<T> {

    @Autowired
    AccountRequestDataController accountRequestDataController;

    @Value("${header.xlbginternaluserid}")
    private String xLbgInternalUserId;

    @Value("${header.xlbginternaluserrole")
    private String xLbgInternalUserRole;

    @Value("${header.xlbgtxncorrelationid")
    private String xLbgTxnCorrelationId;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    AccountRequestDataServiceImpl accountRequestDataService;

    private static boolean setupDone = false;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        if(setupDone) {
            return;
        }
        String insertQuery = "Insert into ACCT_REQUEST(ACCT_REQUEST_ID, ACCOUNT_REQUEST_EXTERNAL_ID, ACCOUNT_REQUEST_JSON_STRING,ACCOUNT_REQUEST_STATUS,CREATED_DATE_TIME,PROVIDER_CLIENT_ID, FAPI_FINANCIAL_ID) values(520,'adf','{" +
                "\"Data\": {" +
                "  \"Permissions\": [" +
                "\"ReadBeneficiariesDetail\", \"ReadDirectDebits\"" +
                "  ]," +
                "  \"ExpirationDateTime\": \"2017-06-01T09:24:30.975Z\"," +
                "  \"TransactionFromDateTime\": \"2017-06-01T09:24:30.975Z\"," +
                "  \"TransactionToDateTime\": \"2017-06-01T09:24:30.975Z\"" +
                "}, " +
                "\"Risk\": {}" +
                "}','AwaitingAuthorisation','2017-12-31 23:59:59','clientid', 'fapi-financial-id-1')";
        String insertHistoryQuery = "Insert into ACCT_REQUEST_STATUS_HIST (ACCT_REQUEST_STATUS_HIST_ID,ACCT_REQUEST_ID,STATUS_UPDATED_DATE_TIME,ACCOUNT_REQUEST_STATUS,STATUS_UPDATED_BY_ROLE) "
        		+ "values (10,520,to_timestamp('17-APR-18 05.43.34.970000000 PM','DD-MON-RR HH.MI.SSXFF AM'),'Authorised','CUSTOMER')";
        jdbcTemplate.execute(insertQuery);
        jdbcTemplate.execute(insertHistoryQuery);

        String insertQuery2 = "Insert into ACCT_REQUEST(ACCT_REQUEST_ID, ACCOUNT_REQUEST_EXTERNAL_ID, ACCOUNT_REQUEST_JSON_STRING,ACCOUNT_REQUEST_STATUS,CREATED_DATE_TIME,PROVIDER_CLIENT_ID, FAPI_FINANCIAL_ID) values(521,'adf1','{" +
                "\"Data\": {" +
                "  \"Permissions\": [" +
                "\"ReadBeneficiariesDetail\", \"ReadDirectDebits\"" +
                "  ]," +
                "  \"ExpirationDateTime\": \"2017-06-01T09:24:30.975Z\"," +
                "  \"TransactionFromDateTime\": \"2017-06-01T09:24:30.975Z\"," +
                "  \"TransactionToDateTime\": \"2017-06-01T09:24:30.975Z\"" +
                "}, " +
                "\"Risk\": {}" +
                "}','AwaitingAuthorisation','2017-12-31 23:59:59','clientid', 'fapi-financial-id-1')";
        String insertHistoryQuery2 = "Insert into ACCT_REQUEST_STATUS_HIST (ACCT_REQUEST_STATUS_HIST_ID,ACCT_REQUEST_ID,STATUS_UPDATED_DATE_TIME,ACCOUNT_REQUEST_STATUS,STATUS_UPDATED_BY_ROLE) "
        		+ "values (11,521,to_timestamp('17-APR-18 05.43.34.970000000 PM','DD-MON-RR HH.MI.SSXFF AM'),'Authorised','CUSTOMER')";
        jdbcTemplate.execute(insertQuery2);
        jdbcTemplate.execute(insertHistoryQuery2);

        String insertQuery3 = "Insert into ACCT_REQUEST(ACCT_REQUEST_ID, ACCOUNT_REQUEST_EXTERNAL_ID, ACCOUNT_REQUEST_JSON_STRING,ACCOUNT_REQUEST_STATUS,CREATED_DATE_TIME,PROVIDER_CLIENT_ID, FAPI_FINANCIAL_ID) values(522,'adf2','{" +
                "\"Data\": {" +
                "  \"Permissions\": [" +
                "\"ReadBeneficiariesDetail\", \"ReadDirectDebits\"" +
                "  ]," +
                "  \"ExpirationDateTime\": \"2017-06-01T09:24:30.975Z\"," +
                "  \"TransactionFromDateTime\": \"2017-06-01T09:24:30.975Z\"," +
                "  \"TransactionToDateTime\": \"2017-06-01T09:24:30.975Z\"" +
                "}, " +
                "\"Risk\": {}" +
                "}','AwaitingAuthorisation','2017-12-31 23:59:59','clientid', 'fapi-financial-id-1')";
        jdbcTemplate.execute(insertQuery3);
        
        String insertHistoryQuery3 = "Insert into ACCT_REQUEST_STATUS_HIST (ACCT_REQUEST_STATUS_HIST_ID,ACCT_REQUEST_ID,STATUS_UPDATED_DATE_TIME,ACCOUNT_REQUEST_STATUS,STATUS_UPDATED_BY_ROLE) "
        		+ "values (12,522,to_timestamp('17-APR-18 05.43.34.970000000 PM','DD-MON-RR HH.MI.SSXFF AM'),'Authorised','CUSTOMER')";
        jdbcTemplate.execute(insertHistoryQuery3);
        setupDone = true;
    }

    @Test
    public void getAccountRequestsTest() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        String selectQuery = "SELECT * FROM   INFORMATION_SCHEMA.TABLES";
        List<Map<String, Object>> resultSet = jdbcTemplate.queryForList(selectQuery);
        assertNotNull(accountRequestDataController.getAccountRequests("123", "adadsaas", false,request, response, "adf2", "clientid").call());

    }

    @Test
    public void getAccountRequestForAccountIdTestWithoutInteractionId() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        AccountRequestOutputData accountRequestOutputData = new AccountRequestOutputData();
        accountRequestOutputData.getAccountRequestExternalIdentifier();
        accountRequestOutputData.getPermissions();
        accountRequestOutputData.getStatus();
        ProviderPermission permission = new ProviderPermission();
        permission.getCode();
        permission.getDescription();
        permission.setCode("ReadAccounts");
        permission.setDescription("");
        AccountRequestStatusHistory accountRequestStatusHistory = new AccountRequestStatusHistory();
        accountRequestStatusHistory.setAccountRequestStatusChangeHistoryId(1L);
        ServicerSchemeNameEnum.BICFI.getValue();
        ServicerSchemeNameEnum.UKSORTCODE.getValue();
        assertNotNull(accountRequestDataController.getAccountRequestForAccountId("123", "adadsaas","client-id-1","fapi-financial-id-1", null,false, request, response, "adf1").call());

    }

    @Test
    public void getAccountRequestForAccountIdTest() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        AccountRequestOutputData accountRequestOutputData = new AccountRequestOutputData();
        accountRequestOutputData.getAccountRequestExternalIdentifier();
        accountRequestOutputData.getPermissions();
        accountRequestOutputData.getStatus();
        ProviderPermission permission = new ProviderPermission();
        permission.getCode();
        permission.getDescription();
        permission.setCode("ReadAccounts");
        permission.setDescription("");
        AccountRequestStatusHistory accountRequestStatusHistory = new AccountRequestStatusHistory();
        accountRequestStatusHistory.setAccountRequestStatusChangeHistoryId(1L);
        accountRequestStatusHistory.setAccountRequestInfoId(2L);
        ServicerSchemeNameEnum.BICFI.getValue();
        ServicerSchemeNameEnum.UKSORTCODE.getValue();
        assertNotNull(accountRequestDataController.getAccountRequestForAccountId("123", "adadsaas","client-id-1","fapi-financial-id-1", "interaction-id-1", false, request, response, "adf").call());

    }

    @Test
    public void getMockEntitlementsForProviderTest() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        CreateAccountInputData createAccountInputData = new CreateAccountInputData();
        createAccountInputData.getPermissions();
        CreateAccountInputRequest createAccountInputRequest = new CreateAccountInputRequest();
        createAccountInputRequest.setCreateAccountInputData(createAccountInputData);
        List<String> permissions = new ArrayList<String>();
        permissions.add(PermissionsEnum.READACCOUNTS.getValue());
        List<String> permissionList = new ArrayList<String>();
        createAccountInputData.setPermissions(permissionList);
        assertNotNull(accountRequestDataController.createAccountRequests("123", "asd", "adf", "fapi-financial-id-1", "interaction-id-1", false,createAccountInputRequest, request, response).call());
    }


    @Test
    public void getMockEntitlementsForProviderTestWithoutFapiInteractionId() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        CreateAccountInputData createAccountInputData = new CreateAccountInputData();
        createAccountInputData.getPermissions();
        CreateAccountInputRequest createAccountInputRequest = new CreateAccountInputRequest();
        createAccountInputRequest.setCreateAccountInputData(createAccountInputData);
        List<String> permissions = new ArrayList<String>();
        permissions.add(PermissionsEnum.READACCOUNTS.getValue());
        List<String> permissionList = new ArrayList<String>();
        createAccountInputData.setPermissions(permissionList);
        assertNotNull(accountRequestDataController.createAccountRequests("123", "asd", "adf", "fapi-financial-id-1",null, false,createAccountInputRequest, request, response).call());
    }

    @Test
    public void updateStatusForAccountRequest() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        UpdateAccountRequestInputData inputData = new UpdateAccountRequestInputData();
        Callable<UpdateAccountRequestOutputData> responseEntityCallable = accountRequestDataController.updateAccountRequestForRequestId( "asd", "adf", false, request, response, "12345", inputData);
        assertNotNull(responseEntityCallable);
    }

    @Test
    public void revokeAccountRequest() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        UpdateAccountRequestInputData inputData = new UpdateAccountRequestInputData();
        accountRequestDataController.deleteAccountRequestForAccountId("CUSTOMER", "adf", "clientid", "financial-id-1", "interaction-id-1", false,new HttpHeaders(),request, response, "adf");
        //no exception we can assume everything went as expected
        assertTrue(true);
    }

    @Test
    public void revokeAccountRequestWithoutFapiInteractionId() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        UpdateAccountRequestInputData inputData = new UpdateAccountRequestInputData();
        accountRequestDataController.deleteAccountRequestForAccountId("CUSTOMER", "adf", "clientid", "financial-id-1", null, false, new HttpHeaders(),request, response, "adf1");
        assertTrue(true);
    }


}