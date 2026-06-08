package com.demo.manual.auth.repository;

import com.demo.manual.auth.model.OAuthClient;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
/** 内存模拟「客户端注册表」，与 client-app 的 application.yml 配置对应 */
@Repository
public class ClientRepository {

    private final Map<String, OAuthClient> clients = new ConcurrentHashMap<>();

    public ClientRepository() {
        // 预置 demo 客户端：secret、回调地址、scope 须与客户端配置一致
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
