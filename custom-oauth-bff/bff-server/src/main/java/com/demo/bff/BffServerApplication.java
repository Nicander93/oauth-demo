package com.demo.bff;

import com.demo.bff.config.OAuthClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(OAuthClientProperties.class)
public class BffServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BffServerApplication.class, args);
    }
}
