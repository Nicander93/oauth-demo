package com.demo.sso.center.repository;

import com.demo.sso.center.model.CenterUser;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {

    private final Map<String, CenterUser> users = new ConcurrentHashMap<>();

    public UserRepository() {
        users.put("alice", new CenterUser("alice", "password", "Alice"));
        users.put("bob", new CenterUser("bob", "password", "Bob"));
    }

    public Optional<CenterUser> findByLoginId(String loginId) {
        return Optional.ofNullable(users.get(loginId));
    }

    public boolean verify(String loginId, String password) {
        return findByLoginId(loginId)
                .map(u -> u.password().equals(password))
                .orElse(false);
    }
}
