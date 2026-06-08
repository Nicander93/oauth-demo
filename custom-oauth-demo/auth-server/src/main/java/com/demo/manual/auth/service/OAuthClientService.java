package com.demo.manual.auth.service;

import com.demo.manual.auth.model.OAuthClient;
import com.demo.manual.auth.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class OAuthClientService {

    private static final Logger log = LoggerFactory.getLogger(OAuthClientService.class);

    private final ClientRepository clientRepository;

    public OAuthClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Optional<OAuthClient> findByClientId(String clientId) {
        return clientRepository.findByClientId(clientId);
    }

    public boolean validateClientSecret(OAuthClient client, String clientSecret) {
        return client.clientSecret().equals(clientSecret);
    }

    public boolean validateRedirectUri(OAuthClient client, String redirectUri) {
        boolean match = client.redirectUris().contains(redirectUri);
        if (!match) {
            log.warn("redirect_uri 校验失败: clientId={}, redirectUri={}", client.clientId(), redirectUri);
        } else {
            log.info("redirect_uri 校验通过: {}", redirectUri);
        }
        return match;
    }

    public boolean validateScopes(OAuthClient client, String scope) {
        if (scope == null || scope.isBlank()) {
            return false;
        }
        Set<String> requested = Set.of(scope.split("\\s+"));
        return client.scopes().containsAll(requested);
    }
}
