package com.demo.manual.client.controller;

import com.demo.manual.client.service.OAuthAuthorizeUrlBuilder;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 客户端发起 OAuth 登录：生成 state 存入 Session，再重定向到授权服务器的 /oauth/authorize。
 */
@Controller
public class OAuthLoginController {

    /** 与回调中的 state 比对，防止授权码被 CSRF 劫持 */
    public static final String SESSION_OAUTH_STATE = "OAUTH_STATE";

    private final OAuthAuthorizeUrlBuilder authorizeUrlBuilder;

    public OAuthLoginController(OAuthAuthorizeUrlBuilder authorizeUrlBuilder) {
        this.authorizeUrlBuilder = authorizeUrlBuilder;
    }

    @GetMapping("/login/oauth")
    public String oauthLogin(HttpSession session) {
        String state = authorizeUrlBuilder.newState();
        session.setAttribute(SESSION_OAUTH_STATE, state);
        return "redirect:" + authorizeUrlBuilder.buildAuthorizeUrl(state);
    }
}
