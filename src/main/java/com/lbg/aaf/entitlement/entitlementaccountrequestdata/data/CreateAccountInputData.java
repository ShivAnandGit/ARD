package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public final class CreateAccountInputData {

    private List<Permission> permissions;
    private String permissionsExpirationDateTime;
    private String transactionFromDateTime;
    private String transactionToDateTime;
    private List<AccountResource> selectedAccounts;
    

    public CreateAccountInputData() {
        //Default constructor
    }
    
    @JsonProperty("Permissions")
    public List<Permission> getPermissions() {
        return permissions;
    }
    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
    
    @JsonProperty("PermissionsExpirationDateTime")
    public String getPermissionsExpirationDateTime() {
        return permissionsExpirationDateTime;
    }
    public void setPermissionsExpirationDateTime(String permissionsExpirationDateTime) {
        this.permissionsExpirationDateTime = permissionsExpirationDateTime;
    }
    
    @JsonProperty("TransactionFromDateTime")
    public String getTransactionFromDateTime() {
        return transactionFromDateTime;
    }
    public void setTransactionFromDateTime(String transactionFromDateTime) {
        this.transactionFromDateTime = transactionFromDateTime;
    }
    
    @JsonProperty("TransactionToDateTime")
    public String getTransactionToDateTime() {
        return transactionToDateTime;
    }
    public void setTransactionToDateTime(String transactionToDateTime) {
        this.transactionToDateTime = transactionToDateTime;
    }
    
    @JsonProperty("SelectedAccounts")
    public List<AccountResource> getSelectedAccounts() {
        return selectedAccounts;
    }

    public void setSelectedAccounts(List<AccountResource> selectedAccounts) {
        this.selectedAccounts = selectedAccounts;
    }
  
}
