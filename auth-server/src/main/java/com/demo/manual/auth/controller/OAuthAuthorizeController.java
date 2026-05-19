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

@Controller
public class OAuthAuthorizeController {

    private final OAuthClientService oAuthClientService;
    private final OAuthCodeService oAuthCodeService;

    public OAuthAuthorizeController(OAuthClientService oAuthClientService,
                                    OAuthCodeService oAuthCodeService) {
        this.oAuthClientService = oAuthClientService;
        this.oAuthCodeService = oAuthCodeService;
    }

    @GetMapping("/oauth/authorize")
    public String authorize(@RequestParam String response_type,
                            @RequestParam String client_id,
                            @RequestParam String redirect_uri,
                            @RequestParam String scope,
                            @RequestParam(required = false) String state,
                            HttpSession session) {
        User loginUser = (User) session.getAttribute(LoginController.SESSION_LOGIN_USER);
        if (loginUser == null) {
            String returnUrl = buildAuthorizeUrl(response_type, client_id, redirect_uri, scope, state);
            session.setAttribute(LoginController.SESSION_OAUTH_PENDING, returnUrl);
            return "redirect:/login";
        }

        if (!"code".equals(response_type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unsupported response_type");
        }

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
