package com.demo.manual.auth.model;

import java.time.Instant;
import java.util.Set;

/** 短期、一次性授权码，浏览器经 redirect 带给客户端，再由后端换 access_token */
public record OAuthCode(
        String code,
        String clientId,
        Long authUserId,
        String username,
        String redirectUri,   // 换 token 时必须与签发时一致
        Set<String> scopes,
        Instant expiresAt,
        boolean used          // 防止重复兑换
) {
}
