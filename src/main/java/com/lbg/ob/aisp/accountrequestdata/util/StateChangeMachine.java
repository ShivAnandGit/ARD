package com.lbg.ob.aisp.accountrequestdata.util;

import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestStatusEnum;

public interface StateChangeMachine {

    public AccountRequestStatusEnum getUpdatableStatus(String currentStatusStr, String possibleStatusStr);

    public AccountRequestStatusEnum getUpdatableStatus(String currentStatusStr, AccountRequestStatusEnum possibleStatus);

    public AccountRequestStatusEnum getUpdatableStatus(AccountRequestStatusEnum currentStatus, AccountRequestStatusEnum possibleStatus);

}
