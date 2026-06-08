package com.demo.sso.systemb.model;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String openid,
        String scope,
        Long expiresIn
) {
}
