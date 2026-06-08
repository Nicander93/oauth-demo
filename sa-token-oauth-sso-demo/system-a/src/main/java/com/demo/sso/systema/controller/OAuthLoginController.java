package com.demo.sso.systema.controller;

import com.demo.sso.systema.service.OAuthAuthorizeUrlBuilder;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuthLoginController {

    public static final String SESSION_OAUTH_STATE = "OAUTH_STATE";

    private final OAuthAuthorizeUrlBuilder authorizeUrlBuilder;

    public OAuthLoginController(OAuthAuthorizeUrlBuilder authorizeUrlBuilder) {
        this.authorizeUrlBuilder = authorizeUrlBuilder;
    }

    @GetMapping("/oauth/login")
    public String oauthLogin(HttpSession session) {
        String state = authorizeUrlBuilder.newState();
        session.setAttribute(SESSION_OAUTH_STATE, state);
        return "redirect:" + authorizeUrlBuilder.buildAuthorizeUrl(state);
    }
}
