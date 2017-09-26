package com.lbg.ob.aisp.accountrequestdata.data;

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
