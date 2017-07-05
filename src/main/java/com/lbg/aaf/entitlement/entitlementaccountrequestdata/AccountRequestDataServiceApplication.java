package com.lbg.aaf.entitlement.entitlementaccountrequestdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.client.AsyncRestTemplate;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Spring Boot Initializer class Uses @SpringBootApplication
 * @author Amit Jain
 */
@Configuration
@SpringBootApplication
@EnableSwagger2
@ComponentScan
@EnableAutoConfiguration
public class AccountRequestDataServiceApplication<T> extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AccountRequestDataServiceApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(AccountRequestDataServiceApplication.class, args);
    }

}