package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public final class Account {


    private AccountSchemeNameEnum schemeName;
    private String identification;
    private String name;
    private String secondaryIdentification;
    
    @JsonProperty("SchemeName")
    public AccountSchemeNameEnum getSchemeName() {
        return schemeName;
    }
    public void setSchemeName(AccountSchemeNameEnum schemeName) {
        this.schemeName = schemeName;
    }
    
    @JsonProperty("Identification")
    public String getIdentification() {
        return identification;
    }
    public void setIdentification(String identification) {
        this.identification = identification;
    }
    
    @JsonProperty("Name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @JsonProperty("SecondaryIdentification")
    public String getSecondaryIdentification() {
        return secondaryIdentification;
    }
    public void setSecondaryIdentification(String secondaryIdentification) {
        this.secondaryIdentification = secondaryIdentification;
    }
    
}
