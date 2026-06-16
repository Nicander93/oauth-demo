package com.demo.manual.client.repository;

import com.demo.manual.client.model.LocalUser;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** 系统 A 本地用户表（与认证中心账号独立） */
@Repository
public class LocalUserRepository {

    private final Map<Long, LocalUser> users = new ConcurrentHashMap<>();
    private final Map<String, Long> usernameIndex = new ConcurrentHashMap<>();

    public LocalUserRepository() {
        save(new LocalUser(1001L, "U10001", "alice", "password", "业务Alice-A"));
    }

    private void save(LocalUser user) {
        users.put(user.id(), user);
        usernameIndex.put(user.username(), user.id());
    }

    public Optional<LocalUser> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<LocalUser> findByUsername(String username) {
        Long id = usernameIndex.get(username);
        return id == null ? Optional.empty() : findById(id);
    }
}
