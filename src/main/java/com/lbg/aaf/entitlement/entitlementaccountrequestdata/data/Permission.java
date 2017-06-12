package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public final class Permission {


    private String code;
    private String description;
    private String sectionHeader;

    @JsonProperty("Code")
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    
    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("SectionHeader")
    public String getSectionHeader() {
        return sectionHeader;
    }
    public void setSectionHeader(String sectionHeader) {
        this.sectionHeader = sectionHeader;
    }
}
