package com.demo.sso.systemb.service;

import com.demo.sso.systemb.config.OAuthClientProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Service
public class OAuthAuthorizeUrlBuilder {

    private final OAuthClientProperties properties;

    public OAuthAuthorizeUrlBuilder(OAuthClientProperties properties) {
        this.properties = properties;
    }

    public String buildAuthorizeUrl(String state) {
        return UriComponentsBuilder
                .fromHttpUrl(properties.authCenterUrl() + "/oauth2/authorize")
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
