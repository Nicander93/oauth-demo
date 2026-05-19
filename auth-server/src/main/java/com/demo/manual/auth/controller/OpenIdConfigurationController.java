package com.demo.manual.auth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * OIDC 发现文档：客户端可读此 JSON 获知 issuer、authorize/token/userinfo 等端点地址。
 */
@RestController
public class OpenIdConfigurationController {

    private final String issuer;

    public OpenIdConfigurationController(@Value("${manual.oidc.issuer}") String issuer) {
        this.issuer = issuer;
    }

    @GetMapping("/.well-known/openid-configuration")
    public Map<String, Object> openidConfiguration() {
        // 本 demo 仅实现授权码 + 部分 OIDC 声明，与生产 IdP 能力集不同
        return Map.of(
                "issuer", issuer,
                "authorization_endpoint", issuer + "/oauth/authorize",
                "token_endpoint", issuer + "/oauth/token",
                "userinfo_endpoint", issuer + "/userinfo",
                "response_types_supported", List.of("code"),
                "subject_types_supported", List.of("public"),
                "id_token_signing_alg_values_supported", List.of("HS256"),
                "scopes_supported", List.of("openid", "profile")
        );
    }
}
