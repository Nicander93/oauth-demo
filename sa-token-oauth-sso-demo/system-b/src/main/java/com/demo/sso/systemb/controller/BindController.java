package com.demo.sso.systemb.controller;

import com.demo.sso.systemb.model.OAuthUserSession;
import com.demo.sso.systemb.service.OAuthLoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BindController {

    public static final String SESSION_PENDING_BIND = "PENDING_OAUTH_BIND";

    private final OAuthLoginService oauthLoginService;

    public BindController(OAuthLoginService oauthLoginService) {
        this.oauthLoginService = oauthLoginService;
    }

    @GetMapping("/bind")
    public String bindPage(HttpSession session, Model model) {
        OAuthLoginService.OAuthLoginResult.NeedBind pending =
                (OAuthLoginService.OAuthLoginResult.NeedBind) session.getAttribute(SESSION_PENDING_BIND);
        if (pending == null) {
            return "redirect:/";
        }
        model.addAttribute("oauthLoginId", pending.loginId());
        model.addAttribute("nickname", pending.nickname());
        return "bind";
    }

    @PostMapping("/bind")
    public String bindSubmit(@RequestParam String username,
                            @RequestParam String password,
                            HttpSession session) {
        OAuthLoginService.OAuthLoginResult.NeedBind pending =
                (OAuthLoginService.OAuthLoginResult.NeedBind) session.getAttribute(SESSION_PENDING_BIND);
        if (pending == null) {
            return "redirect:/";
        }
        try {
            OAuthUserSession user = oauthLoginService.bind(
                    pending.loginId(),
                    pending.openid(),
                    pending.accessToken(),
                    pending.nickname(),
                    username,
                    password);
            session.removeAttribute(SESSION_PENDING_BIND);
            session.setAttribute(HomeController.SESSION_OAUTH_USER, user);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            return "redirect:/bind?error=" + e.getMessage();
        }
    }
}
