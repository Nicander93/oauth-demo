package com.demo.manual.client.service;

import com.demo.manual.client.config.OAuthClientProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

/** 拼装授权请求 URL（Authorization Code 流程的起点，由浏览器访问） */
@Service
public class OAuthAuthorizeUrlBuilder {

    private final OAuthClientProperties properties;

    public OAuthAuthorizeUrlBuilder(OAuthClientProperties properties) {
        this.properties = properties;
    }

    /** response_type=code 表示使用授权码模式 */
    public String buildAuthorizeUrl(String state) {
        return UriComponentsBuilder
                .fromHttpUrl(properties.authServerBaseUrl() + "/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", properties.clientId())
                .queryParam("redirect_uri", properties.redirectUri())
                .queryParam("scope", properties.scope())
                .queryParam("state", state)
                .build()
                .toUriString();
    }

    public String newState() {
        return UUID.randomUUID().toString();
    }
}
