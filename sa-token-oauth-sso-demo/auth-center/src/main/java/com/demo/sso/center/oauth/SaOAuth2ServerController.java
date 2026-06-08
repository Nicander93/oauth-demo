package com.demo.sso.center.oauth;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.demo.sso.center.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SaOAuth2ServerController {

    private final UserRepository userRepository;

    public SaOAuth2ServerController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping("/oauth2/*")
    public Object request() {
        return SaOAuth2ServerProcessor.instance.dister();
    }

    @Autowired
    public void configOAuth2Server(SaOAuth2ServerConfig oauth2Server) {
        SaOAuth2Strategy.instance.notLoginView = () -> new ModelAndView("login.html");

        SaOAuth2Strategy.instance.doLoginHandle = (name, pwd) -> {
            if (userRepository.verify(name, pwd)) {
                StpUtil.login(name);
                return SaResult.ok().set("satoken", StpUtil.getTokenValue());
            }
            return SaResult.error("账号名或密码错误");
        };

        SaOAuth2Strategy.instance.confirmView = (clientId, scopes) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("clientId", clientId);
            map.put("scope", scopes);
            return new ModelAndView("confirm.html", map);
        };
    }
}
