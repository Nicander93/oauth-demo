package com.demo.bff.model;

public record OAuthUserLink(
        String oidcSub,
        Long localUserId
) {
}
