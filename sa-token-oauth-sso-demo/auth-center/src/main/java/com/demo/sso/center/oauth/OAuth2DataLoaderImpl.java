package com.demo.sso.center.oauth;

import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import com.demo.sso.center.repository.ClientRegistry;
import org.springframework.stereotype.Component;

@Component
public class OAuth2DataLoaderImpl implements SaOAuth2DataLoader {

    private final ClientRegistry clientRegistry;

    public OAuth2DataLoaderImpl(ClientRegistry clientRegistry) {
        this.clientRegistry = clientRegistry;
    }

    @Override
    public SaClientModel getClientModel(String clientId) {
        return clientRegistry.getClientModel(clientId);
    }
}
