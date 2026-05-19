package com.demo.manual.auth.repository;

import com.demo.manual.auth.model.AccessToken;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TokenRepository {

    private final Map<String, AccessToken> tokens = new ConcurrentHashMap<>();

    public void save(AccessToken token) {
        tokens.put(token.token(), token);
    }

    public Optional<AccessToken> findByToken(String token) {
        return Optional.ofNullable(tokens.get(token));
    }
}
