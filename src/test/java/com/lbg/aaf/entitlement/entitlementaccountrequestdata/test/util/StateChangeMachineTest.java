package com.lbg.aaf.entitlement.entitlementaccountrequestdata.test.util;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.AccountRequestStatusEnum;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.InvalidRequestException;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.AccountRequestDataConstant;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.StateChangeMachine;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.StateChangeMachineImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StateChangeMachineTest {
    StateChangeMachine stateChangeMachine;

    @Before
    public void setup() {
        stateChangeMachine = new StateChangeMachineImpl();
    }

    @Test
    public void shouldReturnPossibleStateWhenPossibleStateIsInTheListOfPossibleStatesForCurrent() {
        AccountRequestStatusEnum updateableStatus = stateChangeMachine.getUpdatableStatus(AccountRequestDataConstant.AWAITING_AUTHORISATION, AccountRequestDataConstant.AUTHORISED);
        assertEquals(updateableStatus, AccountRequestStatusEnum.AUTHORISED);
    }
    @Test
    public void shouldReturnPossibleStateWhenPossibleStateIsInTheListOfPossibleStatesForCurrent2() {
        AccountRequestStatusEnum updateableStatus = stateChangeMachine.getUpdatableStatus(AccountRequestDataConstant.AWAITING_AUTHORISATION, AccountRequestDataConstant.REJECTED);
        assertEquals(updateableStatus, AccountRequestStatusEnum.REJECTED);
    }

    @Test(expected = InvalidRequestException.class)
    public void shouldThrowExceptioWhenPossibleStateIsntInTheListOfPossibleStatesForTheCurrent() {
        AccountRequestStatusEnum updateableStatus = stateChangeMachine.getUpdatableStatus(AccountRequestDataConstant.AUTHORISED, AccountRequestStatusEnum.AUTHORISED);
    }

    @Test(expected = InvalidRequestException.class)
    public void shouldThrowExceptioWhenPossibleStateIsntInTheListOfPossibleStatesForTheCurrent2() {
        AccountRequestStatusEnum updateableStatus = stateChangeMachine.getUpdatableStatus(AccountRequestDataConstant.REJECTED, AccountRequestStatusEnum.REVOKED);
    }

    @Test(expected = InvalidRequestException.class)
    public void shouldThrowExceptionWhenCurrentStateIsntInTheListOfStates() {
        AccountRequestStatusEnum updateableStatus = stateChangeMachine.getUpdatableStatus("ABCD", AccountRequestStatusEnum.REVOKED);
    }

    @Test(expected = InvalidRequestException.class)
    public void shouldThrowExceptionWhenPossibleStateIsntInTheListOfStates() {
        AccountRequestStatusEnum updateableStatus = stateChangeMachine.getUpdatableStatus(AccountRequestDataConstant.REJECTED, "ABCD");
    }
}
