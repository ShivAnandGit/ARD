package com.lbg.ob.aisp.accountrequestdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.lbg.ob.core.logging.LoggerSpringConfigurationPackage;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableCircuitBreaker
@Configuration
@SpringBootApplication(scanBasePackageClasses = {AccountRequestDataServiceApplication.class, LoggerSpringConfigurationPackage.class})
@EnableSwagger2
//to register InfoController form service info jar 
@ComponentScan(basePackages = {"com.lbg.ob.aisp.accountrequestdata.*", "com.lbg.ob.info.*"})
@EnableAutoConfiguration
@EnableTransactionManagement
//@EnableHystrixDashboard
public class AccountRequestDataServiceApplication<T> extends SpringBootServletInitializer {

    public AccountRequestDataServiceApplication() {
        setRegisterErrorPageFilter(false); // <- this one
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AccountRequestDataServiceApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(AccountRequestDataServiceApplication.class, args);
    }

}