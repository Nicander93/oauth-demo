package com.demo.manual.auth.model;

import java.util.Set;

/** OAuth 2.0 注册客户端 */
public record OAuthClient(
        String clientId,
        String clientSecret,
        Set<String> redirectUris,
        Set<String> scopes
) {
}
