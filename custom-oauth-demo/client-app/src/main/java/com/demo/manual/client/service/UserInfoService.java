package com.demo.manual.client.service;

import com.demo.manual.client.config.OAuthClientProperties;
import com.demo.manual.client.model.UserInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class UserInfoService {

    private final RestClient restClient;
    private final OAuthClientProperties properties;

    public UserInfoService(RestClient restClient, OAuthClientProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public UserInfo fetchUserInfo(String accessToken) {
        return restClient.get()
                .uri(properties.authServerBaseUrl() + "/userinfo")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(UserInfo.class);
    }
}
