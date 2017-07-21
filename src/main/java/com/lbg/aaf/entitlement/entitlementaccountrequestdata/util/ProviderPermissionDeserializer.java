package com.lbg.aaf.entitlement.entitlementaccountrequestdata.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.ProviderPermission;

import java.io.IOException;

/**
 * Created by pbabb1 on 6/21/2017.
 */
public class ProviderPermissionDeserializer extends StdDeserializer<ProviderPermission> {

    public ProviderPermissionDeserializer() {
        this(null);
    }

    public ProviderPermissionDeserializer(Class<?> vc) {
        super(vc);
    }
    @Override
    public ProviderPermission deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode permissionCode= jsonParser.getCodec().readTree(jsonParser);
        ProviderPermission refPerm = new ProviderPermission();
        refPerm.setCode(permissionCode.asText());
        return refPerm;
    }
}
