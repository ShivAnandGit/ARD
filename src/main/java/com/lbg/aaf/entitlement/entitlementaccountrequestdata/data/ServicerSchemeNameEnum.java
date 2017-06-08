package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

public enum ServicerSchemeNameEnum {
    BICFI("BICFI"),UKSORTCODE("UKSortCode");

    private String value;
    public String getValue() {
        return value;
    }
    
    ServicerSchemeNameEnum(String value){
        this.value =value;
    }
}
