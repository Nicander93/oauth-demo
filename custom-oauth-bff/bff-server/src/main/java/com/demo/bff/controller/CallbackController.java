package com.demo.bff.controller;

import com.demo.bff.model.TokenResponse;
import com.demo.bff.model.UserInfo;
import com.demo.bff.service.OAuthLoginService;
import com.demo.bff.service.OAuthTokenService;
import com.demo.bff.service.UserInfoService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class CallbackController {

    private static final Logger log = LoggerFactory.getLogger(CallbackController.class);

    private final OAuthTokenService oAuthTokenService;
    private final UserInfoService userInfoService;
    private final OAuthLoginService oauthLoginService;

    @Value("${manual.bff.ui-base-url:http://localhost:5173}")
    private String uiBaseUrl;

    public CallbackController(OAuthTokenService oAuthTokenService,
                              UserInfoService userInfoService,
                              OAuthLoginService oauthLoginService) {
        this.oAuthTokenService = oAuthTokenService;
        this.userInfoService = userInfoService;
        this.oauthLoginService = oauthLoginService;
    }

    @GetMapping("/callback")
    public String callback(@RequestParam String code,
                           @RequestParam(required = false) String state,
                           HttpSession session) {
        String savedState = (String) session.getAttribute(OAuthLoginController.SESSION_OAUTH_STATE);
        if (savedState == null || !savedState.equals(state)) {
            log.warn("state 校验失败: expected={}, actual={}", savedState, state);
            return "redirect:" + uiUrl("invalid_state", null);
        }
        log.info("state 校验通过");

        TokenResponse tokenResponse = oAuthTokenService.exchangeCode(code);
        UserInfo userInfo = userInfoService.fetchUserInfo(tokenResponse.accessToken());

        OAuthLoginService.OAuthLoginResult result = oauthLoginService.handleCallback(tokenResponse, userInfo);
        session.removeAttribute(OAuthLoginController.SESSION_OAUTH_STATE);

        if (result instanceof OAuthLoginService.OAuthLoginResult.LoggedIn loggedIn) {
            session.setAttribute(AuthApiController.SESSION_LOGIN, loggedIn.session());
            return "redirect:" + uiBaseUrl + "/";
        }
        if (result instanceof OAuthLoginService.OAuthLoginResult.NoLocalUser noLocal) {
            return "redirect:" + uiUrl("no_local_user", noLocal.sub());
        }
        return "redirect:" + uiUrl("login_failed", null);
    }

    private String uiUrl(String error, String sub) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uiBaseUrl + "/")
                .queryParam("error", error);
        if (sub != null) {
            builder.queryParam("sub", sub);
        }
        return builder.build().toUriString();
    }
}
