package com.demo.manual.auth.repository;

import com.demo.manual.auth.model.BizUser;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BizUserRepository {

    private final Map<Long, BizUser> users = new ConcurrentHashMap<>();

    public BizUserRepository() {
        users.put(1001L, new BizUser(1001L, "demo-app", "U10001", "biz_alice", "业务Alice"));
    }

    public Optional<BizUser> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
}
