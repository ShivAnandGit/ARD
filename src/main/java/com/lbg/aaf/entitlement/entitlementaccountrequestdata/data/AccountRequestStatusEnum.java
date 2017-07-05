package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.AccountRequestDataConstant;

import java.util.Arrays;
import java.util.List;

public enum AccountRequestStatusEnum {

    REVOKED(AccountRequestDataConstant.REVOKED), REJECTED(AccountRequestDataConstant.REJECTED), AUTHORISED(AccountRequestDataConstant.AUTHORISED, REVOKED), AWAITINGAUTHORISATION(AccountRequestDataConstant.AWAITING_AUTHORISATION, AUTHORISED, REJECTED, REVOKED) ;

    private String value;
    private List<AccountRequestStatusEnum> allowedTransitions;

    public List<AccountRequestStatusEnum> getAllowedTransitions() {
        return allowedTransitions;
    }


    public String getValue() {
        return value;
    }
    
    AccountRequestStatusEnum(String value, AccountRequestStatusEnum... allowedTransitions){
        this.value =value;
        this.allowedTransitions = Arrays.asList(allowedTransitions);
    }

}
