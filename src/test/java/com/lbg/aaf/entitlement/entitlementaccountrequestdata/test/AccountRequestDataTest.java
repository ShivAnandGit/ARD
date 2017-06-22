package com.lbg.aaf.entitlement.entitlementaccountrequestdata.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.AccountRequestDataController;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.AccountRequestDataServiceApplication;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.service.AccountRequestDataService;

import static org.junit.Assert.*;

/**
 * AccountRequestDataTest Junit
 * @author Sean Wilson
 */

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = {AccountRequestDataServiceApplication.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@EnableWebMvc
public class AccountRequestDataTest<T> {

    @Autowired
    AccountRequestDataController accountRequestDataController;

    @Autowired
    AccountRequestDataService<?> accountRequestDataService;

    @Value("${header.xlbginternaluserid}")
    private String xLbgInternalUserId;

    @Value("${header.xlbginternaluserrole")
    private String xLbgInternalUserRole;

    @Value("${header.xlbgtxncorrelationid")
    private String xLbgTxnCorrelationId;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void getAccountRequestsTest() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        String selectQuery = "SELECT * FROM   INFORMATION_SCHEMA.TABLES";
        List<Map<String, Object>> resultSet = jdbcTemplate.queryForList(selectQuery);
        String insertQuery = "Insert into ACCT_REQUEST(ACCT_REQUEST_ID, ACCOUNT_REQUEST_EXTERNAL_ID, ACCOUNT_REQUEST_JSON_STRING,ACCOUNT_REQUEST_STATUS,CREATED_DATE_TIME,PROVIDER_CLIENT_ID) values(520,'adf','{\"createdDateTime\":1496748849578,\"AccountRequestId\":\"MzBiYzhmNjMtMjllYy00YWU0LWIzN2QtMTE5NGZ\",\"ProviderClientId\":\"tyuyi\",\"Status\":\"AwaitingAuthorisation\",\"Permissions\":[{\"code\":\"ReadAccounts\",\"description\":\"string\"}],\"PermissionsExpirationDateTime\":\"2017-06-01T09:24:30.975Z\",\"TransactionFromDateTime\":\"2017-06-01T09:24:30.975Z\",\"TransactionToDateTime\":\"2017-06-01T09:24:30.975Z\",\"SelectedAccounts\":[{\"Account\":{\"SchemeName\":\"BBAN\",\"Identification\":\"1273\",\"Name\":\"string\",\"SecondaryIdentification\":\"string\"},\"Servicer\":{\"SchemeName\":\"BICFI\",\"Identification\":\"string\"}},{\"Account\":{\"SchemeName\":\"BBAN\",\"Identification\":\"1274\",\"Name\":\"string\",\"SecondaryIdentification\":\"string\"},\"Servicer\":{\"SchemeName\":\"BICFI\",\"Identification\":\"string\"}}]}','AwaitingAuthorisation','2017-12-31 23:59:59','clientid')";
        jdbcTemplate.execute(insertQuery);

        String insertQuery1 = "Insert into ACCT_REQ_ASSOCIATED_ACCT(ACCT_REQ_ASSOCIATED_ACCT_ID, ACCT_REQUEST_ID, ACCOUNT_RESOURCE_IDENTIFIER,CREATED_DATE_TIME) values(520,520,'adf','2017-12-31 23:59:59')";
        jdbcTemplate.execute(insertQuery1);

        // String insertQuery2 = "Insert into ACCT_INFO_STATUS_HISTORY(ACCT_REQ_INFO_STATUS_HIST_ID, ACCT_REQ_INFO_ID, ACCOUNT_REQUEST_STATUS,STATUS_UPDATED_BY,STATUS_UPDATED_BY_ROLE,STATUS_UPDATED_DATE_TIME) values(520,520,'AwaitingAuthorisation',adf','CUSTOMER','2017-12-31 23:59:59')";
        // jdbcTemplate.execute(insertQuery2);
        assertNotNull(accountRequestDataController.getAccountRequests("123", "adadsaas", request, response, "adf", "clientid").call());

    }

    @Test
    public void getAccountRequestForAccountIdTest() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        AccountRequestOutputData accountRequestOutputData = new AccountRequestOutputData();
        accountRequestOutputData.getAccountIds();
        accountRequestOutputData.getAccountRequestExternalIdentifier();
        accountRequestOutputData.getPermissions();
        accountRequestOutputData.getPermissionsExpirationDateTime();
        accountRequestOutputData.getSelectedAccounts();
        accountRequestOutputData.getTransactionFromDateTime();
        accountRequestOutputData.getStatus();
        accountRequestOutputData.getTransactionFromDateTime();
        accountRequestOutputData.getTransactionToDateTime();
        Permission permission = new Permission();
        permission.getCode();
        permission.getDescription();
        permission.setCode("ReadAccounts");
        permission.setDescription("");
        AccountRequestStatusHistory accountRequestStatusHistory = new AccountRequestStatusHistory();
        accountRequestStatusHistory.setAccountRequestStatusChangeHistoryId(1L);
        ServicerSchemeNameEnum.BICFI.getValue();
        ServicerSchemeNameEnum.UKSORTCODE.getValue();
        AccountRequestAssociatedAccountResource associatedAccountsResource = new AccountRequestAssociatedAccountResource();
        associatedAccountsResource.getCreatedDateTime();
        associatedAccountsResource.getAccountRequestInfoId();
        assertNotNull(accountRequestDataController.getAccountRequestForAccountId("123", "adadsaas", request, response, "adf").call());

    }

    @Test
    public void deleteAccountRequestForAccountIdTest() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        ResponseEntity<Void> responseEntity = accountRequestDataController.deleteAccountRequestForAccountId("123", "adadsaas", request, response, "123").call();
        assertTrue(responseEntity.getStatusCode().value() == 204);

    }

    @Test
    public void getMockEntitlementsForProviderTest() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        CreateAccountInputData createAccountInputData = new CreateAccountInputData();
        createAccountInputData.setPermissionsExpirationDateTime("2017-06-01T11:03:49.291Z");
        createAccountInputData.setTransactionFromDateTime("2017-06-01T11:03:49.291Z");
        createAccountInputData.setTransactionToDateTime("2017-06-01T11:03:49.291Z");
        createAccountInputData.getPermissions();
        createAccountInputData.getPermissionsExpirationDateTime();
        createAccountInputData.getSelectedAccounts();
        createAccountInputData.getTransactionFromDateTime();
        createAccountInputData.getTransactionToDateTime();
        List<String> permissions = new ArrayList<String>();
        permissions.add(PermissionsEnum.READACCOUNTS.getValue());
        List<Permission> permissionList = new ArrayList<Permission>();
        createAccountInputData.setPermissions(permissionList);
        AccountResource selectedAccount = new AccountResource();
        Servicer servicer = new Servicer();
        Account account = new Account();
        servicer.setIdentification("123");
        servicer.setSchemeName("SCM");
        account.setIdentification("123");
        account.setName("AJ");
        account.setSchemeName(AccountSchemeNameEnum.BBAN);
        account.setSecondaryIdentification("1");
        servicer.getIdentification();
        servicer.getSchemeName();
        account.getIdentification();
        account.getName();
        account.getSchemeName();
        account.getSecondaryIdentification();

        selectedAccount.setServicer(servicer);
        selectedAccount.setAccount(account);
        selectedAccount.getAccount();
        selectedAccount.getServicer();

        List<AccountResource> selectedAccounts = new ArrayList<AccountResource>();
        selectedAccounts.add(selectedAccount);
        createAccountInputData.setSelectedAccounts(selectedAccounts);
        assertNotNull(accountRequestDataController.createAccountRequests("123", "asd", "adf", createAccountInputData, request, response).call());
    }

    @Test
    public void updateStatusForAccountRequest() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        UpdateAccountInputData inputData = new UpdateAccountInputData();
        inputData.setAccountIds(Arrays.asList("1","2","3"));
        Callable<UpdateRequestOutputData> responseEntityCallable = accountRequestDataController.updateAccountRequestForRequestId("123", "asd", "adf", request, response, "12345", inputData);
        assertNotNull(responseEntityCallable);
    }
}