package com.demo.bff.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "manual.oauth")
public record OAuthClientProperties(
        String clientId,
        String clientSecret,
        String redirectUri,
        String scope,
        String authServerBaseUrl
) {
}
