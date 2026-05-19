package com.demo.manual.auth.model;

import java.time.Instant;
import java.util.Set;

public record OAuthCode(
        String code,
        String clientId,
        Long authUserId,
        String username,
        String redirectUri,
        Set<String> scopes,
        Instant expiresAt,
        boolean used
) {
}
