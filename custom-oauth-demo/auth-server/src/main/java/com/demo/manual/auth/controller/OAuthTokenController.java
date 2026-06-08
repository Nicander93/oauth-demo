package com.demo.manual.auth.controller;

import com.demo.manual.auth.model.OAuthClient;
import com.demo.manual.auth.model.OAuthCode;
import com.demo.manual.auth.model.AccessToken;
import com.demo.manual.auth.service.AccessTokenService;
import com.demo.manual.auth.service.OAuthClientService;
import com.demo.manual.auth.service.OAuthCodeService;
import com.demo.manual.auth.util.Base64Util;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * 令牌端点（Authorization Code 模式第二步）：
 * 客户端用 code + client 凭证换 access_token，code 一次性使用。
 */
@RestController
public class OAuthTokenController {

    private final OAuthClientService oAuthClientService;
    private final OAuthCodeService oAuthCodeService;
    private final AccessTokenService accessTokenService;

    public OAuthTokenController(OAuthClientService oAuthClientService,
                                OAuthCodeService oAuthCodeService,
                                AccessTokenService accessTokenService) {
        this.oAuthClientService = oAuthClientService;
        this.oAuthCodeService = oAuthCodeService;
        this.accessTokenService = accessTokenService;
    }

    /**
     * grant_type=authorization_code；Authorization 头为 Basic(client_id:client_secret)。
     * redirect_uri 须与签发 code 时一致。
     */
    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Map<String, Object>> token(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam String grant_type,
            @RequestParam String code,
            @RequestParam String redirect_uri) {

        if (!"authorization_code".equals(grant_type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unsupported grant_type");
        }

        // 解析 HTTP Basic 中的 client_id:client_secret
        String credentials = Base64Util.decodeBasicAuth(authorization);
        if (credentials == null || !credentials.contains(":")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid client credentials");
        }
        String[] parts = credentials.split(":", 2);
        String clientId = parts[0];
        String clientSecret = parts[1];

        OAuthClient client = oAuthClientService.findByClientId(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid client"));

        if (!oAuthClientService.validateClientSecret(client, clientSecret)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid client secret");
        }

        OAuthCode oauthCode = oAuthCodeService.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid code"));

        if (!oAuthCodeService.validateForToken(oauthCode, clientId, redirect_uri)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid code");
        }

        oAuthCodeService.markUsed(oauthCode); // 防止 code 重复使用
        AccessToken accessToken = accessTokenService.createToken(
                clientId, oauthCode.authUserId(), oauthCode.username(), oauthCode.scopes());

        return ResponseEntity.ok(accessTokenService.toTokenResponse(accessToken));
    }
}
