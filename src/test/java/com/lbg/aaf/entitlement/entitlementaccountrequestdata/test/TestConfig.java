package com.lbg.aaf.entitlement.entitlementaccountrequestdata.test;

import com.lbg.ob.logger.Logger;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Created by pbabb1 on 7/18/17.
 */
@Configuration
public class TestConfig {

    @Bean
    @Primary
    public Logger getLOGGER() {
        Logger LOGGER = Mockito.mock(Logger.class);
        return LOGGER;
    }
}
