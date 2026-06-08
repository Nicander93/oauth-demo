package com.demo.sso.systemb.repository;

import com.demo.sso.systemb.model.OAuthUserLink;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OAuthUserLinkRepository {

    private final ConcurrentHashMap<String, OAuthUserLink> links = new ConcurrentHashMap<>();

    public void save(OAuthUserLink link) {
        links.put(link.oauthLoginId(), link);
    }

    public Optional<OAuthUserLink> findByOAuthLoginId(String oauthLoginId) {
        return Optional.ofNullable(links.get(oauthLoginId));
    }
}
