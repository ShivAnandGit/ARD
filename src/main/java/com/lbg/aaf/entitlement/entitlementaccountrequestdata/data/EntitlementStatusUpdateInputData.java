package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class EntitlementStatusUpdateInputData {
    private Long [] entitlementIdentifiers;

    private String reasonCode;

    private String description;

    public EntitlementStatusUpdateInputData(Long... entitlementIdentifiers) {
        this.entitlementIdentifiers = entitlementIdentifiers;
    }

    @JsonProperty("EntitlementIdentifiers")
    public Long[] getEntitlementIdentifiers() {
        return Arrays.copyOf(entitlementIdentifiers, entitlementIdentifiers.length);
    }

    public void setEntitlementIdentifiers(Long[] entitlementIdentifiers) {
        this.entitlementIdentifiers = Arrays.copyOf(entitlementIdentifiers, entitlementIdentifiers.length);
    }

    @JsonProperty("ReasonCode")
    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
