package com.demo.manual.client.controller;

import com.demo.manual.client.model.ClientLoginSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    public static final String SESSION_LOGIN = "CLIENT_LOGIN";

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        ClientLoginSession login = (ClientLoginSession) session.getAttribute(SESSION_LOGIN);
        model.addAttribute("loggedIn", login != null);
        model.addAttribute("login", login);
        return "index";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
