package com.lbg.aaf.entitlement.entitlementaccountrequestdata;

import com.lbg.ob.logger.Logger;
import com.lbg.ob.logger.factory.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.request.RequestContextListener;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * Spring Boot Initializer class Uses @SpringBootApplication
 * @author Amit Jain
 */
@EnableCircuitBreaker
@Configuration
@SpringBootApplication
@EnableSwagger2
@ComponentScan
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


    @Bean
    public Logger createLogger() {
        return LoggerFactory.getLogger();
    }

//    @Bean
//    public RequestContextListener requestContextListener() throws NamingException {
//        return new RequestContextListener();
//    }

}