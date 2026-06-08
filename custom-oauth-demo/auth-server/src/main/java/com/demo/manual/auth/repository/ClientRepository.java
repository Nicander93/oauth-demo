package com.demo.manual.auth.repository;

import com.demo.manual.auth.model.OAuthClient;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ClientRepository {

    private final Map<String, OAuthClient> clients = new ConcurrentHashMap<>();

    public ClientRepository() {
        clients.put("demo-client", new OAuthClient(
                "demo-client",
                "demo-secret",
                Set.of("http://localhost:8080/callback"),
                Set.of("openid", "profile"),
                "demo-app"
        ));
    }

    public Optional<OAuthClient> findByClientId(String clientId) {
        return Optional.ofNullable(clients.get(clientId));
    }
}
