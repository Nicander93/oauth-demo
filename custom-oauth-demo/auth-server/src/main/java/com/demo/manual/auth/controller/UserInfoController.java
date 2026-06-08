package com.demo.manual.auth.controller;

import com.demo.manual.auth.model.AccessToken;
import com.demo.manual.auth.model.BizUser;
import com.demo.manual.auth.model.OAuthClient;
import com.demo.manual.auth.model.User;
import com.demo.manual.auth.service.AccessTokenService;
import com.demo.manual.auth.service.AuthUserLinkService;
import com.demo.manual.auth.service.OAuthClientService;
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
 * OIDC UserInfo 端点：客户端携带 access_token（Bearer）获取当前用户声明（claims）。
 */
@RestController
public class UserInfoController {

    private static final Logger log = LoggerFactory.getLogger(UserInfoController.class);

    private final AccessTokenService accessTokenService;
    private final UserService userService;
    private final OAuthClientService oAuthClientService;
    private final AuthUserLinkService authUserLinkService;

    public UserInfoController(AccessTokenService accessTokenService,
                              UserService userService,
                              OAuthClientService oAuthClientService,
                              AuthUserLinkService authUserLinkService) {
        this.accessTokenService = accessTokenService;
        this.userService = userService;
        this.oAuthClientService = oAuthClientService;
        this.authUserLinkService = authUserLinkService;
    }

    /** 校验 token 未过期后返回 sub、用户名及可选的业务系统用户映射字段 */
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

        OAuthClient client = oAuthClientService.findByClientId(token.clientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid client"));

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("sub", String.valueOf(user.id()));
        claims.put("preferred_username", user.username());
        claims.put("name", user.nickname());

        authUserLinkService.findBizUser(user.id(), client.systemCode())
                .ifPresent(bizUser -> fillBizClaims(claims, bizUser));

        return claims;
    }

    /** 按 client 所属 systemCode 关联的业务用户扩展信息 */
    private void fillBizClaims(Map<String, Object> claims, BizUser bizUser) {
        claims.put("biz_user_id", bizUser.bizUserCode());
        claims.put("biz_username", bizUser.bizUsername());
        claims.put("biz_nickname", bizUser.bizNickname());
    }
}
