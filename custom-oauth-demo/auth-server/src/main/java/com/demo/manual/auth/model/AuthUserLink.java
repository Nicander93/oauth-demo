package com.demo.manual.auth.model;

public record AuthUserLink(
        Long authUserId,
        String systemCode,
        Long bizUserId
) {
}
