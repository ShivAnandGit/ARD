package com.lbg.ob.aisp.accountrequestdata.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
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
    private String entitlementAccessCode;
    private List<ProviderPermission> permissions;
    private String creationDateTime;
    private String statusUpdateDateTime;
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

    @JsonProperty("CreationDateTime")
    public String getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(String creationDateTime) {
        this.creationDateTime = creationDateTime;
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("EntitlementAccessCode")
	public String getEntitlementAccessCode() {
		return entitlementAccessCode;
	}

	public void setEntitlementAccessCode(String entitlementAccessCode) {
		this.entitlementAccessCode = entitlementAccessCode;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("StatusUpdateDateTime")
	public String getStatusUpdateDateTime() {
		return statusUpdateDateTime;
	}

	public void setStatusUpdateDateTime(String statusUpdateDateTime) {
		this.statusUpdateDateTime = statusUpdateDateTime;
	}
	
	
}
