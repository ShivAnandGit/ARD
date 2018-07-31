package com.lbg.ob.aisp.accountrequestdata.test.util;

import com.lbg.ob.aisp.accountrequestdata.util.Util;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class UtilTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenDateIsNull() {
        String s = Util.formatDateAsISO8601(null);
    }
    
    @Test
    public void shouldFormatDateAsPresentInPayload() {
        String payloadDateTime = "2018-08-01T18:00:30+01:00";
        String expectedValue = "2018-07-31T14:00:30+01:00";
        LocalDateTime localDateTime = Timestamp.valueOf("2018-07-31 14:00:30.523").toLocalDateTime();
        System.out.println(localDateTime);
        String s = Util.formatDate(localDateTime, payloadDateTime);
        assertEquals(expectedValue, s);
    }
    
    @Test
    public void shouldFormatDateWhenUTCFormatPresentInPayload() {
        String payloadDateTime = "2018-09-01T18:00:30.564Z";
        String expectedValue = "2018-07-31T14:00:30.523Z";
        LocalDateTime localDateTime = Timestamp.valueOf("2018-07-31 14:00:30.523").toLocalDateTime();
        System.out.println(localDateTime);
        String s = Util.formatDate(localDateTime, payloadDateTime);
        assertEquals(expectedValue, s);
    }
    
    @Test
    public void shouldFormatDateInISOWhenUTCDatePresentInISOFormatPayloadPresent() {
        String payloadDateTime = "2018-09-01T18:00:30+00:00";
        String expectedValue = "2018-07-31T14:00:30+00:00";
        LocalDateTime localDateTime = Timestamp.valueOf("2018-07-31 14:00:30.523").toLocalDateTime();
        System.out.println(localDateTime);
        String s = Util.formatDate(localDateTime, payloadDateTime);
        assertEquals(expectedValue, s);
    }
}
