package com.demo.manual.auth.controller;

import com.demo.manual.auth.model.PortalApp;
import com.demo.manual.auth.model.User;
import com.demo.manual.auth.repository.PortalAppRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class PortalController {

    private final PortalAppRepository portalAppRepository;

    public PortalController(PortalAppRepository portalAppRepository) {
        this.portalAppRepository = portalAppRepository;
    }

    @GetMapping("/portal")
    public String portal(HttpSession session, Model model) {
        User user = (User) session.getAttribute(LoginController.SESSION_LOGIN_USER);
        if (user == null) {
            return "redirect:/login";
        }
        List<PortalApp> apps = portalAppRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("apps", apps);
        return "portal";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(LoginController.SESSION_LOGIN_USER);
        session.removeAttribute(LoginController.SESSION_OAUTH_PENDING);
        return "redirect:/login";
    }
}
