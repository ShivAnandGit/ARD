package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public final class AccountRequestOutputData {

    private String accountRequestExternalIdentifier;
    private String status;
    private List<String> accountIds;
    private List<Permission> permissions;
    private String permissionsExpirationDateTime;
    private String transactionFromDateTime;
    private String transactionToDateTime;
    private List<AccountResource> selectedAccounts;

    public AccountRequestOutputData() {
        /*
         
         */
    }

    public AccountRequestOutputData(String accountRequestInfoJSONString, List<String> accountIds) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AccountRequestOutputData accountRequestOutputData = mapper.readValue(accountRequestInfoJSONString, AccountRequestOutputData.class);
        this.setAccountRequestExternalIdentifier(accountRequestOutputData.getAccountRequestExternalIdentifier());
        this.setStatus(accountRequestOutputData.getStatus());
        this.setPermissions(accountRequestOutputData.getPermissions());
        this.setPermissionsExpirationDateTime(accountRequestOutputData.getPermissionsExpirationDateTime());
        this.setTransactionFromDateTime(accountRequestOutputData.getTransactionFromDateTime());
        this.setTransactionToDateTime(accountRequestOutputData.getTransactionToDateTime());
        this.setSelectedAccounts(accountRequestOutputData.getSelectedAccounts());
        this.setAccountIds(accountIds);
    }

    @JsonProperty("AccountRequestId")
    public String getAccountRequestExternalIdentifier() {
        return accountRequestExternalIdentifier;
    }

    public void setAccountRequestExternalIdentifier(String accountRequestExternalIdentifier) {
        this.accountRequestExternalIdentifier = accountRequestExternalIdentifier;
    }

    @JsonProperty("Status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("AccountIds")
    public List<String> getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(List<String> accountIds) {
        this.accountIds = accountIds;
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
