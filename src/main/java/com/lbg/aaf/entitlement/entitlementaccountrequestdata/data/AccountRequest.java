package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.Util;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "ACCT_REQUEST")
public final class AccountRequest {

    @Id
    @SequenceGenerator(name = "acct_request_id", sequenceName = "ACCT_REQUEST_SEQ", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acct_request_id")
    @Column(name = "ACCT_REQUEST_ID", updatable = false, nullable = false)
    private Long accountRequestIdentifier;

    @Column(name = "ACCOUNT_REQUEST_EXTERNAL_ID", updatable = false, nullable = false)
    private String accountRequestExternalIdentifier;

    @Column(name = "PROVIDER_CLIENT_ID", updatable = false, nullable = false)
    private String providerClientId;

    @Column(name = "CREATED_DATE_TIME", updatable = false, nullable = false)
    private Timestamp createdDateTime;

    @Column(name = "ACCOUNT_REQUEST_STATUS", updatable = true, nullable = false)
    private String accountRequestStatus;

    @Column(name = "ACCOUNT_REQUEST_JSON_STRING", nullable = false, updatable = true, columnDefinition = "CLOB NOT NULL")
    @Lob
    private String accountRequestJsonString;

    @Transient
    private List<Permission> permissions;
    
    
    @Transient
    private String permissionsExpirationDateTime;
    
    @Transient
    private String transactionFromDateTime;
    
    @Transient
    private String transactionToDateTime;
    
    @Transient
    private List<AccountResource> selectedAccounts;
    
    
    public AccountRequest() {
        //Default constructor
    }

    public AccountRequest(CreateAccountInputData createEntitlementInputData,String clientId) throws JsonProcessingException, UnsupportedEncodingException {
        this.setAccountRequestExternalIdentifier(Util.createUniqueAccountRequestId());
        this.setDefaultAccountRequestStatus();
        this.setPermissions(createEntitlementInputData.getPermissions());
        this.setPermissionsExpirationDateTime(createEntitlementInputData.getPermissionsExpirationDateTime());
        this.setTransactionFromDateTime(createEntitlementInputData.getTransactionFromDateTime());
        this.setTransactionToDateTime(createEntitlementInputData.getTransactionToDateTime());
        this.setSelectedAccounts(createEntitlementInputData.getSelectedAccounts());
        this.setCreatedDateTime();
        this.setProviderClientId(clientId);
        this.setAccountRequestJsonString();
        
    }

    private void setDefaultAccountRequestStatus() {
        this.accountRequestStatus = AccountRequestStatusEnum.AWAITINGAUTHORISATION.getValue();
        
    }

    @JsonProperty("AccountRequestIdentifier")
    public Long getAccountRequestIdentifier() {
        return accountRequestIdentifier;
    }

    public void setAccountRequestIdentifier(Long accountRequestIdentifier) {
        this.accountRequestIdentifier = accountRequestIdentifier;
    }

    @JsonProperty("AccountRequestId")
    public String getAccountRequestExternalIdentifier() {
        return accountRequestExternalIdentifier;
    }

    public void setAccountRequestExternalIdentifier(String accountRequestExternalIdentifier) {
        this.accountRequestExternalIdentifier = accountRequestExternalIdentifier;
    }

    @JsonProperty("ProviderClientId")
    public String getProviderClientId() {
        return providerClientId;
    }

    public void setProviderClientId(String providerClientId) {
        this.providerClientId = providerClientId;
    }

    public Timestamp getCreatedDateTime() {
        return (Timestamp) createdDateTime.clone();
    }

    private void setCreatedDateTime() {
        this.createdDateTime = Util.getCurrentTimestamp();
    }

    @JsonProperty("Status")
    public String getAccountRequestStatus() {
        return accountRequestStatus;
    }

    public void setAccountRequestStatus(AccountRequestStatusEnum status) {
        this.accountRequestStatus = status.getValue();
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

    
    @JsonIgnore
    public String getAccountRequestJsonString() {
        return accountRequestJsonString;
    }

    public void setAccountRequestJsonString() throws JsonProcessingException {
        this.accountRequestJsonString = toJsonString();
    }

    private String toJsonString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(this);

        return jsonString;
    }

}
