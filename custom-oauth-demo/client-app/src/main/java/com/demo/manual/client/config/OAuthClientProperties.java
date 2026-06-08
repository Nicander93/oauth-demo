package com.demo.manual.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** 绑定 application.yml 中 manual.oauth.*，须与 auth-server 注册的客户端一致 */
@ConfigurationProperties(prefix = "manual.oauth")
public record OAuthClientProperties(
        String clientId,
        String clientSecret,
        String redirectUri,
        String scope,
        String authServerBaseUrl
) {
}
