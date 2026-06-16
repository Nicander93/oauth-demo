package com.demo.manual.client.service;

import com.demo.manual.client.config.OAuthClientProperties;
import com.demo.manual.client.model.UserInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
/** 使用 access_token 调用 OIDC UserInfo 端点获取用户声明 */
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
