package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

public enum AccountRequestStatusEnum {

    REJECTED("Rejected"), AUTHENTICATED("Authenticated"),AWAITINGAUTHORISATION("AwaitingAuthorisation");
    
    private String value;
    public String getValue() {
        return value;
    }
    
    AccountRequestStatusEnum(String value){
        this.value =value;
    }

}
