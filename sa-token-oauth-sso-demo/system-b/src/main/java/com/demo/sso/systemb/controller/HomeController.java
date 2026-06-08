package com.demo.sso.systemb.controller;

import com.demo.sso.systemb.model.LocalUserSession;
import com.demo.sso.systemb.model.OAuthUserSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    public static final String SESSION_OAUTH_USER = "OAUTH_USER";
    public static final String SESSION_LOCAL_USER = "LOCAL_USER";

    @GetMapping("/")
    public String home(HttpSession session, Model model,
                       @RequestParam(required = false) String error) {
        model.addAttribute("oauthUser", session.getAttribute(SESSION_OAUTH_USER));
        model.addAttribute("localUser", session.getAttribute(SESSION_LOCAL_USER));
        model.addAttribute("error", error);
        return "index";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/logout/oauth")
    public String logoutOAuth(HttpSession session) {
        session.removeAttribute(SESSION_OAUTH_USER);
        return "redirect:/";
    }

    @PostMapping("/logout/local")
    public String logoutLocal(HttpSession session) {
        session.removeAttribute(SESSION_LOCAL_USER);
        return "redirect:/";
    }
}
