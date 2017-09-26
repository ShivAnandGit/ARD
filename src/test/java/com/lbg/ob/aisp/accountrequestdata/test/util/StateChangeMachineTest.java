package com.lbg.ob.aisp.accountrequestdata.test.util;

import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestStatusEnum;
import com.lbg.ob.aisp.accountrequestdata.exception.InvalidRequestException;
import com.lbg.ob.aisp.accountrequestdata.util.AccountRequestDataConstant;
import com.lbg.ob.aisp.accountrequestdata.util.StateChangeMachine;
import com.lbg.ob.aisp.accountrequestdata.util.StateChangeMachineImpl;
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
