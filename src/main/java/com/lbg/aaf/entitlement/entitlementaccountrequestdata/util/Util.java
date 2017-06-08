package com.lbg.aaf.entitlement.entitlementaccountrequestdata.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

public final class Util {

    private Util() {
        //Default private constructor
    }

    public static Timestamp getCurrentTimestamp() {
        Timestamp timestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
        return timestamp;
    }

    public static String createUniqueAccountRequestId() throws UnsupportedEncodingException {
        UUID uuid = UUID.randomUUID();
        String s = uuid.toString();
        String encodedString = URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8.toString());
        return encodedString;

    }

}
