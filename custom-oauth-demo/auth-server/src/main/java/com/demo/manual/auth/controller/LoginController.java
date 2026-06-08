package com.demo.manual.auth.controller;

import com.demo.manual.auth.model.User;
import com.demo.manual.auth.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * 授权服务器本地登录页（与 OAuth 客户端 Session 分离）。
 * 登录成功后若有挂起的授权请求，则跳回 {@code /oauth/authorize} 继续发 code。
 */
@Controller
public class LoginController {

    /** 当前在授权服务器已认证的用户 */
    public static final String SESSION_LOGIN_USER = "LOGIN_USER";
    /** 登录前访问 /oauth/authorize 时暂存的完整 URL */
    public static final String SESSION_OAUTH_PENDING = "OAUTH_PENDING";

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "用户名或密码错误");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {
        Optional<User> user = loginService.authenticate(username, password);
        if (user.isEmpty()) {
            return "redirect:/login?error=1";
        }
        session.setAttribute(SESSION_LOGIN_USER, user.get());

        @SuppressWarnings("unchecked")
        String pending = (String) session.getAttribute(SESSION_OAUTH_PENDING);
        if (pending != null) {
            session.removeAttribute(SESSION_OAUTH_PENDING);
            return "redirect:" + pending;
        }
        return "redirect:/login?success=1";
    }
}
