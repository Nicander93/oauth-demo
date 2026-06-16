package com.demo.bff.repository;

import com.demo.bff.model.OAuthUserLink;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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
