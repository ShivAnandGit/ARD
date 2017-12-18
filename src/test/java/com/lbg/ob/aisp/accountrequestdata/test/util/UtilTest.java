package com.lbg.ob.aisp.accountrequestdata.test.util;

import com.lbg.ob.aisp.accountrequestdata.util.Util;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;


public class UtilTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenDateIsNull() {
        String s = Util.formatDateAsISO8601(null);
    }
}
