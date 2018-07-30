package com.lbg.ob.aisp.accountrequestdata.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.UUID;

import static com.lbg.ob.aisp.accountrequestdata.util.AccountRequestDataConstant.TIMEZONE;

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

    public static String formatDateAsISO8601(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            throw new IllegalArgumentException();
        }
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(TIMEZONE));
        String formatDateTime = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return formatDateTime;
    }

    public static String formatDate(LocalDateTime localDateTime, String payloadDateTime) {
        if (localDateTime == null) {
            throw new IllegalArgumentException();
        }
        String formatDateTime;
        if (payloadDateTime != null && !payloadDateTime.isEmpty()) {
            if(ZoneOffset.UTC.equals(ZonedDateTime.parse(payloadDateTime).getOffset()) && ZonedDateTime.parse(payloadDateTime).getNano() >= 0) {
                formatDateTime = localDateTime.atZone(ZonedDateTime.parse(payloadDateTime).getZone()).toString();

            }else {
                formatDateTime = localDateTime.atZone(ZonedDateTime.parse(payloadDateTime).getZone()).withNano(0).toString();
            }
        } else {
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault()).withNano(0);
            formatDateTime = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }

        return formatDateTime;
    }
}
