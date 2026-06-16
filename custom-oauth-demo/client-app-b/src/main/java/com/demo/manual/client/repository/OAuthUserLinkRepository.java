package com.demo.manual.client.repository;

import com.demo.manual.client.model.OAuthUserLink;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** 系统 B：sub → 本地用户 绑定表 */
@Repository
public class OAuthUserLinkRepository {

    private final ConcurrentHashMap<String, OAuthUserLink> links = new ConcurrentHashMap<>();

    public void save(OAuthUserLink link) {
        links.put(link.oidcSub(), link);
    }

    public Optional<OAuthUserLink> findByOidcSub(String oidcSub) {
        return Optional.ofNullable(links.get(oidcSub));
    }
}
