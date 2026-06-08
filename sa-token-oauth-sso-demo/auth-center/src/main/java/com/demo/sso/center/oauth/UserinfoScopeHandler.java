package com.demo.sso.center.oauth;

import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.scope.handler.SaOAuth2ScopeHandlerInterface;
import com.demo.sso.center.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class UserinfoScopeHandler implements SaOAuth2ScopeHandlerInterface {

    private final UserRepository userRepository;

    public UserinfoScopeHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String getHandlerScope() {
        return "userinfo";
    }

    @Override
    public void workAccessToken(AccessTokenModel at) {
        String loginId = String.valueOf(at.loginId);
        Map<String, Object> userinfo = new LinkedHashMap<>();
        userinfo.put("loginId", loginId);
        userinfo.put("sub", loginId);
        userRepository.findByLoginId(loginId).ifPresent(u -> userinfo.put("nickname", u.nickname()));
        at.extraData.put("userinfo", userinfo);
    }

    @Override
    public void workClientToken(ClientTokenModel ct) {
    }

    @Override
    public boolean refreshAccessTokenIsWork() {
        return true;
    }
}
