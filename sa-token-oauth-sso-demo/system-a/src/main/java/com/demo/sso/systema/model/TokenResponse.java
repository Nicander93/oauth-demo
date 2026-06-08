package com.demo.sso.systema.model;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String openid,
        String scope,
        Long expiresIn
) {
}
