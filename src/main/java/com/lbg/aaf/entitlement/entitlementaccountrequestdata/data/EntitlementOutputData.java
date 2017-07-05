package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class EntitlementOutputData {
    private Long entitlementId;
    private String updatedEntitlementStatus;
    private String updatedAtTimestamp;
    private String updatedByInternalUserId;

    @JsonProperty("EntitlementId")
    public Long getEntitlementId() {
        return entitlementId;
    }

    public void setEntitlementId(Long entitlementId) {
        this.entitlementId = entitlementId;
    }

    @JsonProperty("UpdatedEntitlementStatus")
    public String getUpdatedEntitlementStatus() {
        return updatedEntitlementStatus;
    }

    public void setUpdatedEntitlementStatus(String updatedEntitlementStatus) {
        this.updatedEntitlementStatus = updatedEntitlementStatus;
    }

    @JsonProperty("UpdatedAtTimestamp")
    public String getUpdatedAtTimestamp() {
        return updatedAtTimestamp;
    }

    public void setUpdatedAtTimestamp(String updatedAtTimestamp) {
        this.updatedAtTimestamp = updatedAtTimestamp;
    }

    @JsonProperty("UpdatedByInternalUserId")
    public String getUpdatedByInternalUserId() {
        return updatedByInternalUserId;
    }

    public void setUpdatedByInternalUserId(String updatedByInternalUserId) {
        this.updatedByInternalUserId = updatedByInternalUserId;
    }
}
