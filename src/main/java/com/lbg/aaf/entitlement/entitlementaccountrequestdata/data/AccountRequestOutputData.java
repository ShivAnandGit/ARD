package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public final class AccountRequestOutputData {

    private String accountRequestExternalIdentifier;
    private String status;
    private List<ProviderPermission> permissions;
    private Map<String, Object> metadata = new HashMap<>();

    public AccountRequestOutputData() {
        /*
         
         */
    }

    public AccountRequestOutputData(String accountRequestInfoJSONString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AccountRequestOutputData accountRequestOutputData = mapper.readValue(accountRequestInfoJSONString, AccountRequestOutputData.class);
        this.setAccountRequestExternalIdentifier(accountRequestOutputData.getAccountRequestExternalIdentifier());
        this.setStatus(accountRequestOutputData.getStatus());
        this.setPermissions(accountRequestOutputData.getPermissions());
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


    @JsonProperty("Permissions")
    public List<ProviderPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<ProviderPermission> permissions) {
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
