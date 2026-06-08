package com.demo.sso.systemb.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(OAuthClientProperties.class)
public class AppConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
