package com.demo.sso.systema.controller;

import com.demo.sso.systema.model.OAuthUserSession;
import com.demo.sso.systema.model.TokenResponse;
import com.demo.sso.systema.service.SaOAuthTokenClient;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CallbackController {

    private static final Logger log = LoggerFactory.getLogger(CallbackController.class);

    private final SaOAuthTokenClient tokenClient;

    public CallbackController(SaOAuthTokenClient tokenClient) {
        this.tokenClient = tokenClient;
    }

    @GetMapping("/callback")
    public String callback(@RequestParam String code,
                           @RequestParam(required = false) String state,
                           HttpSession session) {
        String savedState = (String) session.getAttribute(OAuthLoginController.SESSION_OAUTH_STATE);
        if (savedState == null || !savedState.equals(state)) {
            log.warn("state 校验失败");
            return "redirect:/?error=invalid_state";
        }

        try {
            TokenResponse token = tokenClient.exchangeCode(code);
            JsonNode userinfo = unwrapUserinfo(tokenClient.fetchUserinfo(token.accessToken()));
            String loginId = text(userinfo, "loginId");
            if (loginId == null) {
                loginId = text(userinfo, "sub");
            }
            if (loginId == null) {
                loginId = token.openid();
            }
            String nickname = text(userinfo, "nickname");
            if (nickname == null) {
                nickname = loginId;
            }

            session.setAttribute(HomeController.SESSION_OAUTH_USER,
                    new OAuthUserSession(loginId, token.openid(), token.accessToken(), nickname));
            session.removeAttribute(OAuthLoginController.SESSION_OAUTH_STATE);
            return "redirect:/";
        } catch (Exception e) {
            log.error("OAuth 回调失败", e);
            return "redirect:/?error=" + e.getMessage();
        }
    }

    private static JsonNode unwrapUserinfo(JsonNode data) {
        if (data.has("userinfo") && data.path("userinfo").isObject()) {
            return data.path("userinfo");
        }
        return data;
    }

    private static String text(JsonNode node, String field) {
        JsonNode v = node.path(field);
        return v.isMissingNode() || v.isNull() ? null : v.asText();
    }
}
