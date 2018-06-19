package com.lbg.ob.aisp.accountrequestdata.test;

import com.lbg.ob.aisp.accountrequestdata.exception.EntitlementUpdateFailedException;
import com.lbg.ob.aisp.accountrequestdata.exception.ResourceAccessException;
import com.lbg.ob.aisp.accountrequestdata.service.EntitlementProxyServiceImpl;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import net.jadler.stubbing.server.jdk.JdkStubHttpServer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.concurrent.ExecutionException;

import static com.lbg.ob.aisp.accountrequestdata.service.EntitlementProxyServiceImpl.X_LBG_UPDATED_BY_USER_ID;
import static com.lbg.ob.aisp.accountrequestdata.util.AccountRequestDataConstant.X_LBG_INTERNAL_USER_ID;
import static com.lbg.ob.aisp.accountrequestdata.util.AccountRequestDataConstant.X_LBG_INTERNAL_USER_ROLE;
import static net.jadler.Jadler.closeJadler;
import static net.jadler.Jadler.initJadlerUsing;
import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.port;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

public class EntitlementServiceTest {

    String clientId = "abcd";

    @Mock
    AsyncRestTemplate restTemplate;

//    @Mock
//    private static final Logger logger = LogManager.getLogger(AccountRequestDataServiceTest.class);
    
    @InjectMocks
    EntitlementProxyServiceImpl entitlementService;
    private Boolean fovIndicator = false;

    @Before
    public void init()  {
        initJadlerUsing(new JdkStubHttpServer());
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        closeJadler();
    }

    @Test
    public void shouldReturnSuccessWhenEntitlementIsRevokedSuccessfully() throws Exception {
        String internalUserRole = "some-role";
        String userId = "SYSTEM";
        HttpHeaders headers = new HttpHeaders();
        long entitlementId = 23L;
        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo("/entitlements/status/revoke")
                .havingHeaderEqualTo("Content-Type", "application/json")
                .havingHeaderEqualTo(X_LBG_INTERNAL_USER_ID, userId)
                .havingHeaderEqualTo(X_LBG_UPDATED_BY_USER_ID, clientId)
                .havingHeaderEqualTo(X_LBG_INTERNAL_USER_ROLE, internalUserRole )
                .havingBodyEqualTo(requestBody())
                .respond()
                .withBody(successJson())
                .withHeader("Content-Type", "application/json;charset=UTF-8")
                .withStatus(200);
        
        entitlementService.setRequestURL("http://localhost:"+port()+"/entitlements/status/revoke");
        
        entitlementService.revokeEntitlement(entitlementId, userId, internalUserRole, clientId, fovIndicator,headers.toSingleValueMap());
        assertTrue(true);
    }

    @Test(expected = ExecutionException.class)
    public void shouldThrowExceptionWhenEntitlementRevokedResponseIsnt200() throws Exception {
        String internalUserRole = "some-role";
        String userId = "SYSTEM";
        long entitlementId = 23L;
        HttpHeaders headers = new HttpHeaders();

        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo("/entitlements/status/revoke")
                .havingHeaderEqualTo("Content-Type", "application/json")
                .havingHeaderEqualTo(X_LBG_INTERNAL_USER_ID, userId)
                .havingHeaderEqualTo(X_LBG_UPDATED_BY_USER_ID, clientId)
                .havingHeaderEqualTo(X_LBG_INTERNAL_USER_ROLE, internalUserRole )
                .havingBodyEqualTo(requestBody())
                .respond()
                .withBody(failJson())
                .withHeader("Content-Type", "application/json;charset=UTF-8")
                .withStatus(404);
        entitlementService.setRequestURL("http://localhost:"+port()+"/entitlements/status/revoke");
        entitlementService.revokeEntitlement(entitlementId, userId, internalUserRole, clientId, fovIndicator,headers.toSingleValueMap());
    }

    @Test(expected = EntitlementUpdateFailedException.class)
    public void shouldThrowExceptionWhenEntitlementStatusIsntRevoked() throws Exception {
        String correlationId = "corelation-id";
        String internalUserRole = "some-role";
        String userId = "SYSTEM";
        HttpHeaders headers = new HttpHeaders();
        long entitlementId = 23L;
        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo("/entitlements/status/revoke")
                .havingHeaderEqualTo("Content-Type", "application/json")
                .havingHeaderEqualTo(X_LBG_INTERNAL_USER_ID, userId)
                .havingHeaderEqualTo(X_LBG_UPDATED_BY_USER_ID, clientId)
                .havingHeaderEqualTo(X_LBG_INTERNAL_USER_ROLE, internalUserRole )
                .havingBodyEqualTo(requestBody())
                .respond()
                .withBody(notRevokedBody())
                .withHeader("Content-Type", "application/json;charset=UTF-8")
                .withStatus(200);
        entitlementService.setRequestURL("http://localhost:"+port()+"/entitlements/status/revoke");
        entitlementService.revokeEntitlement(entitlementId, userId, internalUserRole, clientId, fovIndicator,headers.toSingleValueMap());
    }


    @Test(expected = ExecutionException.class)
    public void shouldThrowExceptionWhenEntitlementRevokedResponseIsnt200AndTheResponseIsNotProper() throws Exception {
        String correlationId = "corelation-id";
        String internalUserRole = "some-role";
        String userId = "SYSTEM";
        HttpHeaders headers = new HttpHeaders();
        long entitlementId = 23L;

        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo("/entitlements/status/revoke")
                .havingHeaderEqualTo("Content-Type", "application/json")
                .havingHeaderEqualTo("x-lbg-txn-correlation-id", correlationId)
                .havingHeaderEqualTo(X_LBG_INTERNAL_USER_ID, userId)
                .havingHeaderEqualTo(X_LBG_UPDATED_BY_USER_ID, clientId)
                .havingHeaderEqualTo(X_LBG_INTERNAL_USER_ROLE, internalUserRole )
                .havingBodyEqualTo(requestBody())
                .respond()
                .withBody(failBadJson())
                .withHeader("Content-Type", "application/json;charset=UTF-8")
                .withStatus(404);
        entitlementService.setRequestURL("http://localhost:"+port()+"/entitlements/status/revoke");
        entitlementService.revokeEntitlement(entitlementId, userId, internalUserRole, correlationId, fovIndicator,headers.toSingleValueMap());
    }

    @Test(expected = ResourceAccessException.class)
    public void shouldThrowResourceAccessExceptionForFallback() throws Exception {
        Throwable ex = new HystrixTimeoutException();
//        Mockito.doNothing().when(logger).error(anyString(), any(Throwable.class));
        Whitebox.invokeMethod(entitlementService,"fallback", ex);
    }

    @Test(expected = EntitlementUpdateFailedException.class)
    public void shouldThrowEntitlementUpdateFailedExceptionForFallback() throws Exception {
        ExecutionException ex = new ExecutionException("test ex", new EntitlementUpdateFailedException("test msg"));
//        Mockito.doNothing().when(logger).error(anyString(), any(Throwable.class));
        Whitebox.invokeMethod(entitlementService,"fallback", ex);
    }

    @Test(expected = ResourceAccessException.class)
    public void shouldThrowResourceAccessExceptionForFallback2() throws Exception {
        ExecutionException ex = new ExecutionException("test ex", new Exception("test msg"));
//        Mockito.doNothing().when(LOGGER).logException(anyString(), any(Throwable.class));
        Whitebox.invokeMethod(entitlementService,"fallback", ex);
    }

    private byte[] notRevokedBody() {
        String s = "[\n" +
                "  {\n" +
                "    \"EntitlementId\": 23,\n" +
                "    \"UpdatedEntitlementStatus\": \"ACTIVE\",\n" +
                "    \"UpdatedAtTimestamp\": \"2017-10-30T18:08:20.301Z\",\n" +
                "    \"UpdatedByInternalUserId\": \"string\"\n" +
                "  }\n" +
                "]";
        return s.getBytes();
    }

    private String requestBody() {
        return "{\"EntitlementIdentifiers\":[23]}";
    }

    private byte[] failJson() {
        String res = "{\n" +
                "    \"error\": {\n" +
                "        \"statusCode\": 404,\n" +
                "        \"code\": \"EES_API_ERR_004\",\n" +
                "        \"message\": \"Entitlement not found\"\n" +
                "    }\n" +
                "}";
        return res.getBytes();
    }

    private byte[] failBadJson() {
        String res = "{\"something\":\"something\"}";
        return res.getBytes();
    }

    private byte[] successJson() {
        String res = "[\n" +
                "  {\n" +
                "    \"EntitlementId\": 23,\n" +
                "    \"UpdatedEntitlementStatus\": \"REVOKED\",\n" +
                "    \"UpdatedAtTimestamp\": \"2017-10-30T18:08:20.301Z\",\n" +
                "    \"UpdatedByInternalUserId\": \"string\"\n" +
                "  }\n" +
                "]";
        return res.getBytes();
    }
}
