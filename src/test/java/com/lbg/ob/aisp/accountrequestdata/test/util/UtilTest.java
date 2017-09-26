package com.lbg.ob.aisp.accountrequestdata.test.util;

import com.lbg.ob.aisp.accountrequestdata.util.Util;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;


public class UtilTest {
    @Test
    public void shouldFormatTheDateAsISO8601() {
        String s = Util.formatDateAsISO8601(LocalDateTime.now());
        assertTrue(s.matches("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]T[0-9][0-9]:[0-9][0-9]:[0-9][0-9].[0-9][0-9][0-9]Z"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenDateIsNull() {
        String s = Util.formatDateAsISO8601(null);
    }
}
