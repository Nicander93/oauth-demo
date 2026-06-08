package com.demo.manual.client.controller;

import com.demo.manual.client.model.TokenResponse;
import com.demo.manual.client.model.UserInfo;
import com.demo.manual.client.service.OAuthTokenService;
import com.demo.manual.client.service.UserInfoService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CallbackController {

    private static final Logger log = LoggerFactory.getLogger(CallbackController.class);

    private final OAuthTokenService oAuthTokenService;
    private final UserInfoService userInfoService;

    public CallbackController(OAuthTokenService oAuthTokenService, UserInfoService userInfoService) {
        this.oAuthTokenService = oAuthTokenService;
        this.userInfoService = userInfoService;
    }

    @GetMapping("/callback")
    public String callback(@RequestParam String code,
                           @RequestParam(required = false) String state,
                           HttpSession session) {
        String savedState = (String) session.getAttribute(OAuthLoginController.SESSION_OAUTH_STATE);
        if (savedState == null || !savedState.equals(state)) {
            log.warn("state 校验失败: expected={}, actual={}", savedState, state);
            return "redirect:/?error=invalid_state";
        }
        log.info("state 校验通过");

        TokenResponse tokenResponse = oAuthTokenService.exchangeCode(code);
        UserInfo userInfo = userInfoService.fetchUserInfo(tokenResponse.accessToken());

        session.setAttribute(HomeController.SESSION_ACCESS_TOKEN, tokenResponse.accessToken());
        session.setAttribute(HomeController.SESSION_ID_TOKEN, tokenResponse.idToken());
        session.setAttribute(HomeController.SESSION_USER_INFO, userInfo);
        session.removeAttribute(OAuthLoginController.SESSION_OAUTH_STATE);

        return "redirect:/";
    }
}
