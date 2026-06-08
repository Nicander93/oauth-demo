package com.demo.oauth.resource.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserInfoController {

    @GetMapping("/userinfo")
    public Map<String, Object> userinfo(@AuthenticationPrincipal Jwt jwt) {
        // Jwt 由资源服务器自动解析并注入
        Map<String, Object> body = new HashMap<>();
        // 以下字段来自授权服务器签发 access token 时写入的 claims
        body.put("id", jwt.getClaim("user_id"));
        body.put("username", jwt.getSubject());
        body.put("nickname", jwt.getClaim("nickname"));
        return body;
    }
}
