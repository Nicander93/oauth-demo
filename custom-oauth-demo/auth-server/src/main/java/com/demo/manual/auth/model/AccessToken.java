package com.demo.manual.auth.model;

import java.time.Instant;
import java.util.Set;

public record AccessToken(
        String token,
        String clientId,
        Long authUserId,
        String username,
        Set<String> scopes,
        Instant expiresAt
) {
}
