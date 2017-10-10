package com.lbg.ob.aisp.accountrequestdata.test;

import com.lbg.ob.aisp.accountrequestdata.data.EntitlementOutputData;
import com.lbg.ob.aisp.accountrequestdata.exception.EntitlementUpdateFailedException;
import com.lbg.ob.aisp.accountrequestdata.exception.ResourceAccessException;
import com.lbg.ob.aisp.accountrequestdata.service.EntitlementProxyServiceImpl;
import com.lbg.ob.logger.Logger;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
@RunWith(PowerMockRunner.class)
@PrepareForTest({ EntitlementProxyServiceImpl.class })
public class EntitlementServiceTest {

    String requestURL = "someurl";

    @Mock
    Logger LOGGER;

    @Mock
    AsyncRestTemplate restTemplate;

    @InjectMocks
    EntitlementProxyServiceImpl entitlementService;

    @Before
    public void init()  {
        MockitoAnnotations.initMocks(this);
        doNothing().when(LOGGER).logTrace(anyString(), anyString());
        doNothing().when(LOGGER).logInfo(anyString(), anyString());
        doNothing().when(LOGGER).logException(anyString(), any(Exception.class));
    }

    @Test
    public void shouldReturnSuccessWhenEntitlementIsRevokedSuccessfully() throws Exception {
        String correlationId = "corelation-id";
        String internalUserRole = "some-role";
        String userId = "SYSTEM";
        long entitlementId = 1234L;
        PowerMockito.whenNew(AsyncRestTemplate.class).withNoArguments().thenReturn(restTemplate);

        ListenableFuture<ResponseEntity<EntitlementOutputData[]>> future = getListenableFuture("Revoked", HttpStatus.OK, null);

        when(restTemplate.exchange(anyString(), Mockito.<HttpMethod> any(), Mockito.<HttpEntity> any(), Mockito.<Class<EntitlementOutputData[]>> any())).thenReturn(future);
        entitlementService.revokeEntitlement(entitlementId, userId, internalUserRole, correlationId);
        assertTrue(true);
    }

    @Test(expected = EntitlementUpdateFailedException.class)
    public void shouldThrowExceptionWhenEntitlementRevokedResponseIsnt200() throws Exception {
        String correlationId = "corelation-id";
        String internalUserRole = "some-role";
        String userId = "SYSTEM";
        long entitlementId = 1234L;
        PowerMockito.whenNew(AsyncRestTemplate.class).withNoArguments().thenReturn(restTemplate);

        ListenableFuture<ResponseEntity<EntitlementOutputData[]>> future = getListenableFuture("Revoked", HttpStatus.BAD_REQUEST, null);
        when(restTemplate.exchange(anyString(), Mockito.<HttpMethod> any(), Mockito.<HttpEntity> any(), Mockito.<Class<EntitlementOutputData[]>> any())).thenReturn(future);
        entitlementService.revokeEntitlement(entitlementId, userId, internalUserRole, correlationId);
    }

    @Test(expected = EntitlementUpdateFailedException.class)
    public void shouldThrowExceptionWhenEntitlementStatusIsntRevoked() throws Exception {
        String correlationId = "corelation-id";
        String internalUserRole = "some-role";
        String userId = "SYSTEM";
        long entitlementId = 1234L;
        PowerMockito.whenNew(AsyncRestTemplate.class).withNoArguments().thenReturn(restTemplate);

        ListenableFuture<ResponseEntity<EntitlementOutputData[]>> future = getListenableFuture("Active", HttpStatus.OK, null);
        when(restTemplate.exchange(anyString(), Mockito.<HttpMethod> any(), Mockito.<HttpEntity> any(), Mockito.<Class<EntitlementOutputData[]>> any())).thenReturn(future);
        entitlementService.revokeEntitlement(entitlementId, userId, internalUserRole, correlationId);
    }

    @Test(expected = ResourceAccessException.class)
    public void shouldThrowResourceAccessExceptionForFallback() throws Exception {
        Throwable ex = new HystrixTimeoutException();
        Mockito.doNothing().when(LOGGER).logException(anyString(), any(Throwable.class));
        Whitebox.invokeMethod(entitlementService,"fallback", ex);
    }


    private ListenableFuture<ResponseEntity<EntitlementOutputData[]>> getListenableFuture(String revokedStatus, HttpStatus status, final Throwable ex) {
        EntitlementOutputData entitlement = new EntitlementOutputData();
        entitlement.setEntitlementId(1234L);
        entitlement.setUpdatedEntitlementStatus(revokedStatus);
        EntitlementOutputData[] entArr = {entitlement};
        final ResponseEntity<EntitlementOutputData[]> entities = new ResponseEntity<EntitlementOutputData[]>(entArr, status);
        return new ListenableFuture<ResponseEntity<EntitlementOutputData[]>>() {
            @Override
            public void addCallback(ListenableFutureCallback<? super ResponseEntity<EntitlementOutputData[]>> listenableFutureCallback) {

            }

            @Override
            public void addCallback(SuccessCallback<? super ResponseEntity<EntitlementOutputData[]>> successCallback, FailureCallback failureCallback) {

            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public ResponseEntity<EntitlementOutputData[]> get() throws InterruptedException, ExecutionException {
                if(ex != null) {
                    if(ex instanceof InterruptedException) {
                        throw (InterruptedException) ex;
                    } else if (ex instanceof ExecutionException) {
                        throw (ExecutionException) ex;
                    }
                }
                return entities;
            }

            @Override
            public ResponseEntity<EntitlementOutputData[]> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }
        };
    }
}