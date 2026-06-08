package com.demo.manual.auth.service;

import com.demo.manual.auth.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** 授权服务器表单登录校验（demo 明文密码，仅用于学习） */
@Service
public class LoginService {

    private final UserService userService;

    public LoginService(UserService userService) {
        this.userService = userService;
    }

    public Optional<User> authenticate(String username, String password) {
        return userService.findByUsername(username)
                .filter(user -> user.password().equals(password));
    }
}
