package com.demo.manual.auth.repository;

import com.demo.manual.auth.model.OAuthClient;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** 内存模拟「客户端注册表」，与 client-app-a / client-app-b 的 application.yml 配置对应 */
@Repository
public class ClientRepository {

    private final Map<String, OAuthClient> clients = new ConcurrentHashMap<>();

    public ClientRepository() {
        clients.put("demo-client", new OAuthClient(
                "demo-client",
                "demo-secret",
                Set.of("http://localhost:8060/callback"),
                Set.of("openid", "profile")
        ));
        clients.put("demo-client-b", new OAuthClient(
                "demo-client-b",
                "demo-secret-b",
                Set.of("http://localhost:8061/callback"),
                Set.of("openid", "profile")
        ));
        clients.put("demo-client-bff", new OAuthClient(
                "demo-client-bff",
                "demo-secret-bff",
                Set.of("http://localhost:8070/callback"),
                Set.of("openid", "profile")
        ));
    }

    public Optional<OAuthClient> findByClientId(String clientId) {
        return Optional.ofNullable(clients.get(clientId));
    }
}
