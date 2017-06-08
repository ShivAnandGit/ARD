package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public final class AccountResource {


    private Account account;
    private Servicer servicer;
    
    @JsonProperty("Account")
    public Account getAccount() {
        return account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }
    
    @JsonProperty("Servicer")
    public Servicer getServicer() {
        return servicer;
    }
    public void setServicer(Servicer servicer) {
        this.servicer = servicer;
    }
    
    
}
