package com.demo.manual.auth.repository;

import com.demo.manual.auth.model.AuthUserLink;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
/** 演示：auth 用户 alice(id=1) 关联 demo-app 业务用户 */
@Repository
public class AuthUserLinkRepository {

    private final ConcurrentHashMap<String, AuthUserLink> links = new ConcurrentHashMap<>();

    public AuthUserLinkRepository() {
        save(new AuthUserLink(1L, "demo-app", 1001L));
    }

    public void save(AuthUserLink link) {
        links.put(key(link.authUserId(), link.systemCode()), link);
    }

    public Optional<AuthUserLink> findByAuthUserIdAndSystemCode(Long authUserId, String systemCode) {
        return Optional.ofNullable(links.get(key(authUserId, systemCode)));
    }

    private String key(Long authUserId, String systemCode) {
        return authUserId + ":" + systemCode;
    }
}
