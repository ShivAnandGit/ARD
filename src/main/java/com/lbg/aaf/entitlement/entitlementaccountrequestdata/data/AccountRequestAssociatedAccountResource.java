package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.Util;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "ACCT_REQ_ASSOCIATED_ACCT")
public final class AccountRequestAssociatedAccountResource {

    @Id
    @SequenceGenerator(name = "acct_req_associated_acct_id", sequenceName = "ACCT_REQ_ASSOCIATED_ACCT_SEQ", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acct_req_associated_acct_id")
    @Column(name = "ACCT_REQ_ASSOCIATED_ACCT_ID", updatable = false, nullable = false)
    private Long accountRequestAssociatedAccountIdentifier;

    @Column(name = "ACCT_REQUEST_ID", updatable = false, nullable = false)
    private Long accountRequestInfoId;

    @Column(name = "ACCOUNT_RESOURCE_IDENTIFIER", updatable = false, nullable = false)
    private String accountIdentifier;

    @Column(name = "CREATED_DATE_TIME", updatable = false, nullable = false)
    private Timestamp createdDateTime;

    public AccountRequestAssociatedAccountResource() {
        this.setCreatedDateTime();
    }

    @JsonProperty("AccountIdentifier")
    public Long getAccountRequestAssociatedAccountIdentifier() {
        return accountRequestAssociatedAccountIdentifier;
    }

    @JsonIgnore
    public Long getAccountRequestInfoId() {
        return accountRequestInfoId;
    }

    public void setAccountRequestInfoId(Long accountRequestInfoId) {
        this.accountRequestInfoId = accountRequestInfoId;
    }

    @JsonProperty("AccountIdentifier")
    public String getAccountIdentifier() {
        return accountIdentifier;
    }

    public void setAccountIdentifier(String accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }

    @JsonProperty("CreatedDateTime")
    public Timestamp getCreatedDateTime() {
        return (Timestamp) createdDateTime.clone();
    }

    public void setCreatedDateTime() {
        this.createdDateTime = Util.getCurrentTimestamp();
    }
}
