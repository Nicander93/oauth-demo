package com.demo.sso.systemb.controller;

import com.demo.sso.systemb.model.OAuthUserSession;
import com.demo.sso.systemb.service.OAuthLoginService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CallbackController {

    private static final Logger log = LoggerFactory.getLogger(CallbackController.class);

    private final OAuthLoginService oauthLoginService;

    public CallbackController(OAuthLoginService oauthLoginService) {
        this.oauthLoginService = oauthLoginService;
    }

    @GetMapping("/callback")
    public String callback(@RequestParam String code,
                           @RequestParam(required = false) String state,
                           HttpSession session) {
        String savedState = (String) session.getAttribute(OAuthLoginController.SESSION_OAUTH_STATE);
        if (savedState == null || !savedState.equals(state)) {
            return "redirect:/?error=invalid_state";
        }
        session.removeAttribute(OAuthLoginController.SESSION_OAUTH_STATE);

        try {
            OAuthLoginService.OAuthLoginResult result = oauthLoginService.handleCallback(code);
            if (result instanceof OAuthLoginService.OAuthLoginResult.LoggedIn loggedIn) {
                session.setAttribute(HomeController.SESSION_OAUTH_USER, loggedIn.session());
                return "redirect:/";
            }
            OAuthLoginService.OAuthLoginResult.NeedBind needBind =
                    (OAuthLoginService.OAuthLoginResult.NeedBind) result;
            session.setAttribute(BindController.SESSION_PENDING_BIND, needBind);
            return "redirect:/bind";
        } catch (Exception e) {
            log.error("OAuth 回调失败", e);
            return "redirect:/?error=" + e.getMessage();
        }
    }
}
