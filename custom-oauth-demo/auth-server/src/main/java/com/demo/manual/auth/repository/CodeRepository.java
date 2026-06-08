package com.demo.manual.auth.repository;

import com.demo.manual.auth.model.OAuthCode;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
/** 内存存储已签发的 authorization code（生产环境应落库并支持集群） */
@Repository
public class CodeRepository {

    private final Map<String, OAuthCode> codes = new ConcurrentHashMap<>();

    public void save(OAuthCode code) {
        codes.put(code.code(), code);
    }

    public Optional<OAuthCode> findByCode(String code) {
        return Optional.ofNullable(codes.get(code));
    }

    public void update(OAuthCode code) {
        codes.put(code.code(), code);
    }
}
