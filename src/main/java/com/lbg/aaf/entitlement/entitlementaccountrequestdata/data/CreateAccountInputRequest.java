package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

import com.fasterxml.jackson.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CreateAccountInputRequest {
    @Valid
    private CreateAccountInputData createAccountInputData;
    private Map<String, Object> metadata = new HashMap<>();

    @JsonProperty("Data")
    public CreateAccountInputData getCreateAccountInputData() {
        return createAccountInputData;
    }

    public void setCreateAccountInputData(CreateAccountInputData createAccountInputData) {
        this.createAccountInputData = createAccountInputData;
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
