package com.demo.sso.center.repository;

import cn.dev33.satoken.oauth2.consts.GrantType;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClientRegistry {

    private final List<SaClientModel> clients = new ArrayList<>();

    public ClientRegistry() {
        clients.add(buildClient(
                "system-a",
                "system-a-secret",
                "http://localhost:9201/callback"));
        clients.add(buildClient(
                "system-b",
                "system-b-secret",
                "http://localhost:9202/callback"));
    }

    private SaClientModel buildClient(String clientId, String secret, String redirectUri) {
        return new SaClientModel()
                .setClientId(clientId)
                .setClientSecret(secret)
                .addAllowRedirectUris(redirectUri)
                .addContractScopes("openid", "userinfo")
                .setIsAutoConfirm(true)
                .addAllowGrantTypes(
                        GrantType.authorization_code,
                        GrantType.refresh_token);
    }

    public SaClientModel getClientModel(String clientId) {
        return clients.stream()
                .filter(c -> c.getClientId().equals(clientId))
                .findFirst()
                .orElse(null);
    }
}
