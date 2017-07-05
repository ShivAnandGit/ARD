package com.lbg.aaf.entitlement.entitlementaccountrequestdata.util;


import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.AccountRequestStatusEnum;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.InvalidRequestException;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.ExceptionConstants.ARD_API_ERR_007;
import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.ExceptionConstants.BAD_REQUEST_INVALID_REQUEST;

@Component
public final class StateChangeMachineImpl implements StateChangeMachine {

    @Override
    public AccountRequestStatusEnum getUpdatableStatus(String currentStatusStr, String possibleStatusStr) {
        AccountRequestStatusEnum currentStatus = getAccountRequestStatusEnum(currentStatusStr);
        AccountRequestStatusEnum possibleStatus = getAccountRequestStatusEnum(possibleStatusStr);
        return getUpdatableStatus(currentStatus, possibleStatus);
    }

    @Override
    public AccountRequestStatusEnum getUpdatableStatus(String currentStatusStr, AccountRequestStatusEnum possibleStatus) {
        AccountRequestStatusEnum currentStatus = getAccountRequestStatusEnum(currentStatusStr);
        return getUpdatableStatus(currentStatus, possibleStatus);
    }

    @Override
    public AccountRequestStatusEnum getUpdatableStatus(AccountRequestStatusEnum currentStatus, AccountRequestStatusEnum possibleStatus) {
        List<AccountRequestStatusEnum> allowedTransitions = currentStatus.getAllowedTransitions();
        if(!allowedTransitions.contains(possibleStatus)) {
            throw new InvalidRequestException(BAD_REQUEST_INVALID_REQUEST, ARD_API_ERR_007);
        }
        return possibleStatus;
    }

    private AccountRequestStatusEnum getAccountRequestStatusEnum(String state) {
        AccountRequestStatusEnum accountRequestStatusEnum;
        try {
            accountRequestStatusEnum = AccountRequestStatusEnum.valueOf(state.toUpperCase());
        } catch(IllegalArgumentException ex) {
            throw new InvalidRequestException(BAD_REQUEST_INVALID_REQUEST, ARD_API_ERR_007, ex);
        }
        return accountRequestStatusEnum;
    }
}
