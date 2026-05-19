package com.demo.manual.auth.model;

import java.util.Set;

public record OAuthClient(
        String clientId,
        String clientSecret,
        Set<String> redirectUris,
        Set<String> scopes,
        String systemCode
) {
}
