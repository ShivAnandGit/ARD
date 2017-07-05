package com.lbg.aaf.entitlement.entitlementaccountrequestdata.test;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.EntitlementOutputData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.EntitlementUpdateFailedException;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.service.EntitlementServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
@RunWith(PowerMockRunner.class)
@PrepareForTest({ EntitlementServiceImpl.class })
public class EntitlementServiceTest {

    String requestURL = "someurl";

    @Mock
    AsyncRestTemplate restTemplate;

    @InjectMocks
    EntitlementServiceImpl entitlementService;

    @Before
    public void init()  {
        MockitoAnnotations.initMocks(this);
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

    @Test(expected = EntitlementUpdateFailedException.class)
    public void shouldThrowExceptionWhenEntitlementCallIsInterrupted() throws Exception {
        String correlationId = "corelation-id";
        String internalUserRole = "some-role";
        String userId = "SYSTEM";
        long entitlementId = 1234L;
        PowerMockito.whenNew(AsyncRestTemplate.class).withNoArguments().thenReturn(restTemplate);

        ListenableFuture<ResponseEntity<EntitlementOutputData[]>> future = getListenableFuture("Active", HttpStatus.OK, new InterruptedException());
        when(restTemplate.exchange(anyString(), Mockito.<HttpMethod> any(), Mockito.<HttpEntity> any(), Mockito.<Class<EntitlementOutputData[]>> any())).thenReturn(future);
        entitlementService.revokeEntitlement(entitlementId, userId, internalUserRole, correlationId);
    }

    @Test(expected = EntitlementUpdateFailedException.class)
    public void shouldThrowExceptionWhenEntitlementCallHasExecutionProblems() throws Exception {
        String correlationId = "corelation-id";
        String internalUserRole = "some-role";
        String userId = "SYSTEM";
        long entitlementId = 1234L;
        PowerMockito.whenNew(AsyncRestTemplate.class).withNoArguments().thenReturn(restTemplate);

        ListenableFuture<ResponseEntity<EntitlementOutputData[]>> future = getListenableFuture("Active", HttpStatus.OK, new ExecutionException("", new Exception()));
        when(restTemplate.exchange(anyString(), Mockito.<HttpMethod> any(), Mockito.<HttpEntity> any(), Mockito.<Class<EntitlementOutputData[]>> any())).thenReturn(future);
        entitlementService.revokeEntitlement(entitlementId, userId, internalUserRole, correlationId);
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
