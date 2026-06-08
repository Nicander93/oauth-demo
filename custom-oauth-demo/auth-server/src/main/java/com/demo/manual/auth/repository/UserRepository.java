package com.demo.manual.auth.repository;

import com.demo.manual.auth.model.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
/** 授权中心用户（演示账号 alice / password） */
@Repository
public class UserRepository {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    public UserRepository() {
        users.put("alice", new User(1L, "alice", "password", "Alice"));
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public Optional<User> findById(Long id) {
        return users.values().stream()
                .filter(user -> user.id().equals(id))
                .findFirst();
    }
}
