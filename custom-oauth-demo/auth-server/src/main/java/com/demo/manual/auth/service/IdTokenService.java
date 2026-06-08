package com.demo.manual.auth.service;

import com.demo.manual.auth.model.User;
import com.demo.manual.auth.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * 签发 OIDC id_token（JWT）。
 * 本 demo 用 HS256 + 共享密钥，生产环境通常用 RS256 并由客户端通过 JWKS 验签。
 */
@Service
public class IdTokenService {

    private static final Logger log = LoggerFactory.getLogger(IdTokenService.class);

    private final String issuer;
    private final String jwtSecret;
    private final long tokenExpiresSeconds;

    public IdTokenService(@Value("${manual.oidc.issuer}") String issuer,
                          @Value("${manual.oidc.jwt-secret}") String jwtSecret,
                          @Value("${manual.oauth.token-expires-seconds:3600}") long tokenExpiresSeconds) {
        this.issuer = issuer;
        this.jwtSecret = jwtSecret;
        this.tokenExpiresSeconds = tokenExpiresSeconds;
    }

    public String createIdToken(User user, String clientId) {
        Instant now = Instant.now();
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("iss", issuer);           // 签发者
        claims.put("sub", String.valueOf(user.id()));  // 主体（用户唯一标识）
        claims.put("aud", clientId);         // 受众（本 token 发给哪个 client）
        claims.put("iat", now.getEpochSecond());
        claims.put("exp", now.plusSeconds(tokenExpiresSeconds).getEpochSecond());
        claims.put("preferred_username", user.username());
        claims.put("name", user.nickname());

        String idToken = JwtUtil.createHs256(claims, jwtSecret);
        log.info("生成 id_token: sub={}, aud={}", user.id(), clientId);
        return idToken;
    }
}
