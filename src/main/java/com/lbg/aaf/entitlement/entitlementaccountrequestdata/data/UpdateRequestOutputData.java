package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class UpdateRequestOutputData implements Serializable {
    private String accountRequestId;
    private String updatedStatus;
    private String updatedAtTimestamp;

    @JsonProperty("AccountRequestId")
    public String getAccountRequestId() {
        return accountRequestId;
    }

    public void setAccountRequestId(String accountRequestId) {
        this.accountRequestId = accountRequestId;
    }

    @JsonProperty("UpdatedStatus")
    public String getUpdatedStatus() {
        return updatedStatus;
    }

    public void setUpdatedStatus(String updatedStatus) {
        this.updatedStatus = updatedStatus;
    }

    @JsonProperty("UpdatedAtTimestamp")
    public String getUpdatedAtTimestamp() {
        return updatedAtTimestamp;
    }

    public void setUpdatedAtTimestamp(String updatedAtTimestamp) {
        this.updatedAtTimestamp = updatedAtTimestamp;
    }
}
