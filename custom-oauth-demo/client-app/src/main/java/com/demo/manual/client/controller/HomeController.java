package com.demo.manual.client.controller;

import com.demo.manual.client.model.UserInfo;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 客户端首页：展示 OAuth 登录结果（Session 中的 token 与用户信息）。
 */
@Controller
public class HomeController {

    public static final String SESSION_USER_INFO = "USER_INFO";
    public static final String SESSION_ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String SESSION_ID_TOKEN = "ID_TOKEN";

    /** 从 Session 读取登录态，渲染 index 模板 */
    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        UserInfo userInfo = (UserInfo) session.getAttribute(SESSION_USER_INFO);
        String accessToken = (String) session.getAttribute(SESSION_ACCESS_TOKEN);
        String idToken = (String) session.getAttribute(SESSION_ID_TOKEN);
        model.addAttribute("loggedIn", userInfo != null);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("accessToken", accessToken);
        model.addAttribute("idToken", idToken);
        return "index";
    }

    /** 清除本地 Session（本 demo 不调用授权服务器登出端点） */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
