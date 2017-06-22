package com.lbg.aaf.entitlement.entitlementaccountrequestdata.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.AccountRequestDataConstant.ISO8601_DATE_FORMAT;
import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.AccountRequestDataConstant.TIMEZONE;

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

    public static String formatDateAsISO8601(Long time) {
        if (time == null) {
            throw new IllegalArgumentException();
        } else {
            TimeZone tz = TimeZone.getTimeZone(TIMEZONE);
            DateFormat df = new SimpleDateFormat(ISO8601_DATE_FORMAT);
            df.setTimeZone(tz);
            String isoDateFormatString = df.format(new Date(time));
            return isoDateFormatString;
        }
    }
}
