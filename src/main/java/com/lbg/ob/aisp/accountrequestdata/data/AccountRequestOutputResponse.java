package com.lbg.ob.aisp.accountrequestdata.data;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbg.ob.aisp.accountrequestdata.util.Util;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class AccountRequestOutputResponse {
    private AccountRequestOutputData accountRequestOutputData;

    private Map<String, Object> metadata =  new HashMap<>();

    public AccountRequestOutputResponse(){
        //DEFAULT CONSTRUCTOR NEEDED FOR JACKSON UNMARSHALLING
    }

    public AccountRequestOutputResponse(String jsonString) throws IOException {

    }

    public AccountRequestOutputResponse(String accountRequestExternalIdentifier, String accountRequestStatus, Timestamp createdDateTime, String accountRequestJsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AccountRequestOutputResponse accountRequestOutputResponse = mapper.readValue(accountRequestJsonString, AccountRequestOutputResponse.class);
        AccountRequestOutputData outputData = accountRequestOutputResponse.getAccountRequestOutputData();
        outputData.setCreationDateTime(Util.formatDateAsISO8601(createdDateTime.toLocalDateTime()));
        outputData.setAccountRequestExternalIdentifier(accountRequestExternalIdentifier);
        outputData.setStatus(accountRequestStatus);
        this.setAccountRequestOutputData(outputData);
        this.metadata = accountRequestOutputResponse.metadata;
    }

    @JsonProperty("Data")
    public AccountRequestOutputData getAccountRequestOutputData() {
        return accountRequestOutputData;
    }

    private void setAccountRequestOutputData(AccountRequestOutputData accountRequestOutputData) {
        this.accountRequestOutputData = accountRequestOutputData;
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
