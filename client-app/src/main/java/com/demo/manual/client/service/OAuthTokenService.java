package com.demo.manual.client.service;

import com.demo.manual.client.config.OAuthClientProperties;
import com.demo.manual.client.model.TokenResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class OAuthTokenService {

    private final RestClient restClient;
    private final OAuthClientProperties properties;

    public OAuthTokenService(RestClient restClient, OAuthClientProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public TokenResponse exchangeCode(String code) {
        String credentials = properties.clientId() + ":" + properties.clientSecret();
        String basicAuth = "Basic " + Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", properties.redirectUri());

        return restClient.post()
                .uri(properties.authServerBaseUrl() + "/oauth/token")
                .header("Authorization", basicAuth)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(TokenResponse.class);
    }
}
