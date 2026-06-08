package com.demo.sso.systema.controller;

import com.demo.sso.systema.model.OAuthUserSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    public static final String SESSION_OAUTH_USER = "OAUTH_USER";

    @GetMapping("/")
    public String home(HttpSession session, Model model,
                       @org.springframework.web.bind.annotation.RequestParam(required = false) String error) {
        OAuthUserSession user = (OAuthUserSession) session.getAttribute(SESSION_OAUTH_USER);
        model.addAttribute("user", user);
        model.addAttribute("error", error);
        return "index";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
