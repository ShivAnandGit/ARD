package com.lbg.aaf.entitlement.entitlementaccountrequestdata.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().httpStrictTransportSecurity().includeSubDomains(true).maxAgeInSeconds(31536000)
                .and().contentTypeOptions()
                .and().frameOptions();
        http.csrf().disable();
    }

}