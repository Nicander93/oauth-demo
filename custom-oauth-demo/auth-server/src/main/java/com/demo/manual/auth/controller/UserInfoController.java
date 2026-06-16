package com.demo.manual.auth.controller;

import com.demo.manual.auth.model.AccessToken;
import com.demo.manual.auth.model.User;
import com.demo.manual.auth.service.AccessTokenService;
import com.demo.manual.auth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * OIDC UserInfo 端点：仅返回认证中心标准 claims；业务用户由各子系统自行维护与绑定。
 */
@RestController
public class UserInfoController {

    private static final Logger log = LoggerFactory.getLogger(UserInfoController.class);

    private final AccessTokenService accessTokenService;
    private final UserService userService;

    public UserInfoController(AccessTokenService accessTokenService, UserService userService) {
        this.accessTokenService = accessTokenService;
        this.userService = userService;
    }

    @GetMapping("/userinfo")
    public Map<String, Object> userinfo(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "missing bearer token");
        }
        String tokenValue = authorization.substring(7).trim();
        log.info("校验 access_token: {}", tokenValue);

        AccessToken token = accessTokenService.findByToken(tokenValue)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token"));

        if (accessTokenService.isExpired(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "token expired");
        }

        User user = userService.findById(token.authUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user not found"));

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("sub", String.valueOf(user.id()));
        claims.put("preferred_username", user.username());
        claims.put("name", user.nickname());
        return claims;
    }
}
