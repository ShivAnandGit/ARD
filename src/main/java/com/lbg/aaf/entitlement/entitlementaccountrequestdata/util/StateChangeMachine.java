package com.lbg.aaf.entitlement.entitlementaccountrequestdata.util;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.AccountRequestStatusEnum;

public interface StateChangeMachine {

    public AccountRequestStatusEnum getUpdatableStatus(String currentStatusStr, String possibleStatusStr);

    public AccountRequestStatusEnum getUpdatableStatus(String currentStatusStr, AccountRequestStatusEnum possibleStatus);

    public AccountRequestStatusEnum getUpdatableStatus(AccountRequestStatusEnum currentStatus, AccountRequestStatusEnum possibleStatus);

}
