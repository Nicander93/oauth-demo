package com.demo.sso.systemb.service;

import com.demo.sso.systemb.config.OAuthClientProperties;
import com.demo.sso.systemb.model.TokenResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class SaOAuthTokenClient {

    private final RestClient restClient;
    private final OAuthClientProperties properties;

    public SaOAuthTokenClient(RestClient restClient, OAuthClientProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public TokenResponse exchangeCode(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("client_id", properties.clientId());
        body.add("client_secret", properties.clientSecret());
        body.add("redirect_uri", properties.redirectUri());

        String responseBody = restClient.post()
                .uri(properties.authCenterUrl() + "/oauth2/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(String.class);

        JsonNode data = SaResultParser.parse(responseBody);
        return new TokenResponse(
                text(data, "access_token"),
                text(data, "refresh_token"),
                text(data, "openid"),
                text(data, "scope"),
                data.path("expires_in").isNumber() ? data.path("expires_in").asLong() : null
        );
    }

    public JsonNode fetchUserinfo(String accessToken) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("access_token", accessToken);

        String responseBody = restClient.post()
                .uri(properties.authCenterUrl() + "/oauth2/userinfo")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(String.class);

        return SaResultParser.parse(responseBody);
    }

    private static String text(JsonNode data, String field) {
        JsonNode node = data.path(field);
        return node.isMissingNode() || node.isNull() ? null : node.asText();
    }
}
