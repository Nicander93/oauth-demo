package com.demo.manual.auth.controller;

import com.demo.manual.auth.model.OAuthClient;
import com.demo.manual.auth.model.OAuthCode;
import com.demo.manual.auth.model.User;
import com.demo.manual.auth.service.OAuthClientService;
import com.demo.manual.auth.service.OAuthCodeService;
import com.demo.manual.auth.util.UrlUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 授权端点（Authorization Code 模式第一步）。
 * <p>
 * 浏览器被重定向到此地址；用户已在授权服务器登录后，校验 client/redirect_uri/scope，
 * 生成一次性 authorization code，再 302 回客户端的 redirect_uri?code=...&state=...
 * </p>
 */
@Controller
public class OAuthAuthorizeController {

    private final OAuthClientService oAuthClientService;
    private final OAuthCodeService oAuthCodeService;

    public OAuthAuthorizeController(OAuthClientService oAuthClientService,
                                    OAuthCodeService oAuthCodeService) {
        this.oAuthClientService = oAuthClientService;
        this.oAuthCodeService = oAuthCodeService;
    }

    /**
     * 标准参数：response_type=code、client_id、redirect_uri、scope；state 可选（防 CSRF，由客户端生成并校验）。
     */
    @GetMapping("/oauth/authorize")
    public String authorize(@RequestParam String response_type,
                            @RequestParam String client_id,
                            @RequestParam String redirect_uri,
                            @RequestParam String scope,
                            @RequestParam(required = false) String state,
                            HttpSession session) {
        // 未登录：记下完整授权 URL，登录成功后继续走本端点
        User loginUser = (User) session.getAttribute(LoginController.SESSION_LOGIN_USER);
        if (loginUser == null) {
            String returnUrl = buildAuthorizeUrl(response_type, client_id, redirect_uri, scope, state);
            session.setAttribute(LoginController.SESSION_OAUTH_PENDING, returnUrl);
            return "redirect:/login";
        }

        if (!"code".equals(response_type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unsupported response_type");
        }

        // 校验第三方应用身份与回调白名单，防止 code 被劫持到恶意站点
        OAuthClient client = oAuthClientService.findByClientId(client_id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid client_id"));

        if (!oAuthClientService.validateRedirectUri(client, redirect_uri)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid redirect_uri");
        }

        if (!oAuthClientService.validateScopes(client, scope)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid scope");
        }

        Set<String> scopes = Set.of(scope.split("\\s+"));
        OAuthCode oauthCode = oAuthCodeService.createCode(
                client_id, loginUser.id(), loginUser.username(), redirect_uri, scopes);

        // 把 code 通过浏览器重定向交给客户端（后端换 token 时再校验 client_secret）
        Map<String, String> params = new LinkedHashMap<>();
        params.put("code", oauthCode.code());
        if (state != null) {
            params.put("state", state);
        }
        return "redirect:" + UrlUtil.appendQuery(redirect_uri, params);
    }

    private String buildAuthorizeUrl(String responseType, String clientId,
                                     String redirectUri, String scope, String state) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("response_type", responseType);
        params.put("client_id", clientId);
        params.put("redirect_uri", redirectUri);
        params.put("scope", scope);
        if (state != null) {
            params.put("state", state);
        }
        return UrlUtil.appendQuery("/oauth/authorize", params);
    }
}
