package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.validation.PermissionsConstraint;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public final class CreateAccountInputData {
    @PermissionsConstraint
    private List<String> permissions;

    private Map<String, Object> metadata = new HashMap<>();

    public CreateAccountInputData() {
        //Default constructor
    }

    @JsonProperty("Permissions")
    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return metadata;
    }

    @JsonAnySetter
    public void set(String name, Object value) {
        this.metadata.put(name, value);
    }
}
