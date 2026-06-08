package com.demo.sso.systema.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sso.oauth")
public record OAuthClientProperties(
        String authCenterUrl,
        String clientId,
        String clientSecret,
        String redirectUri,
        String scope
) {
}
