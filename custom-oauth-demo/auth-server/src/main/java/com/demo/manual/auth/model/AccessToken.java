package com.demo.manual.auth.model;

import java.time.Instant;
import java.util.Set;

/** 访问受保护资源（如 /userinfo）时使用的 Bearer token */
public record AccessToken(
        String token,
        String clientId,
        Long authUserId,
        String username,
        Set<String> scopes,
        Instant expiresAt
) {
}
