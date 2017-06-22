package com.lbg.aaf.entitlement.entitlementaccountrequestdata.test.util;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.Util;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;


public class UtilTest {
    @Test
    public void shouldFormatTheDateAsISO8601() {
        String s = Util.formatDateAsISO8601(new Date().getTime());
        assertTrue(s.matches("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]T[0-9][0-9]:[0-9][0-9]:[0-9][0-9].[0-9][0-9][0-9]Z"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenDateIsNull() {
        String s = Util.formatDateAsISO8601(null);
    }
}
