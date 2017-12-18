package com.lbg.ob.aisp.accountrequestdata.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lbg.ob.aisp.accountrequestdata.validation.MetadataConstraint;
import com.lbg.ob.aisp.accountrequestdata.validation.PermissionsConstraint;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public final class CreateAccountInputData {
    @PermissionsConstraint
    private List<String> permissions;

    @MetadataConstraint
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

    //Created a getter for the spring framework
    @JsonIgnore
    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
