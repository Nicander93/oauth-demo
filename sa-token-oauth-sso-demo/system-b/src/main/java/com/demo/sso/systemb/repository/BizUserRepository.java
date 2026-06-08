package com.demo.sso.systemb.repository;

import com.demo.sso.systemb.model.BizUser;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BizUserRepository {

    private final Map<Long, BizUser> users = new ConcurrentHashMap<>();
    private final Map<String, Long> usernameIndex = new ConcurrentHashMap<>();

    public BizUserRepository() {
        save(new BizUser(1L, "bob_local", "password", "本地Bob"));
        save(new BizUser(2L, "alice", "password", "本地Alice"));
    }

    private void save(BizUser user) {
        users.put(user.id(), user);
        usernameIndex.put(user.username(), user.id());
    }

    public Optional<BizUser> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<BizUser> findByUsername(String username) {
        Long id = usernameIndex.get(username);
        return id == null ? Optional.empty() : findById(id);
    }

    public boolean verify(String username, String password) {
        return findByUsername(username)
                .map(u -> u.password().equals(password))
                .orElse(false);
    }
}
