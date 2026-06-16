package com.demo.bff.model;

public record MeResponse(
        OidcUserView oidc,
        LocalUserView localUser,
        String accessToken,
        String idToken
) {
    public record OidcUserView(String sub, String username, String name) {
    }

    public record LocalUserView(String userCode, String username, String nickname) {
    }
}
