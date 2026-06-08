package com.demo.sso.systemb.controller;

import com.demo.sso.systemb.model.LocalUserSession;
import com.demo.sso.systemb.repository.BizUserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LocalLoginController {

    private final BizUserRepository bizUserRepository;

    public LocalLoginController(BizUserRepository bizUserRepository) {
        this.bizUserRepository = bizUserRepository;
    }

    @PostMapping("/local/login")
    public String localLogin(@RequestParam String username,
                             @RequestParam String password,
                             HttpSession session) {
        return bizUserRepository.findByUsername(username)
                .filter(u -> u.password().equals(password))
                .map(u -> {
                    session.setAttribute(HomeController.SESSION_LOCAL_USER,
                            new LocalUserSession(u.id(), u.username(), u.nickname()));
                    return "redirect:/";
                })
                .orElse("redirect:/?error=local_login_failed");
    }
}
