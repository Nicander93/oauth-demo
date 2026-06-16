package com.demo.manual.client.controller;

import com.demo.manual.client.model.ClientLoginSession;
import com.demo.manual.client.service.OAuthLoginService;
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
    public String bindPage(HttpSession session, Model model,
                           @RequestParam(required = false) String error) {
        OAuthLoginService.OAuthLoginResult.NeedBind pending =
                (OAuthLoginService.OAuthLoginResult.NeedBind) session.getAttribute(SESSION_PENDING_BIND);
        if (pending == null) {
            return "redirect:/";
        }
        model.addAttribute("sub", pending.sub());
        model.addAttribute("username", pending.userInfo().username());
        model.addAttribute("name", pending.userInfo().name());
        if (error != null) {
            model.addAttribute("error", error);
        }
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
            ClientLoginSession login = oauthLoginService.bind(
                    pending.sub(),
                    pending.userInfo(),
                    pending.accessToken(),
                    pending.idToken(),
                    username,
                    password);
            session.removeAttribute(SESSION_PENDING_BIND);
            session.setAttribute(HomeController.SESSION_LOGIN, login);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            return "redirect:/bind?error=" + e.getMessage();
        }
    }
}
