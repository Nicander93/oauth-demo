package com.demo.sso.systemb.service;

import com.demo.sso.systemb.model.BizUser;
import com.demo.sso.systemb.model.OAuthUserLink;
import com.demo.sso.systemb.model.OAuthUserSession;
import com.demo.sso.systemb.model.TokenResponse;
import com.demo.sso.systemb.repository.BizUserRepository;
import com.demo.sso.systemb.repository.OAuthUserLinkRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OAuthLoginService {

    private final SaOAuthTokenClient tokenClient;
    private final BizUserRepository bizUserRepository;
    private final OAuthUserLinkRepository linkRepository;

    public OAuthLoginService(SaOAuthTokenClient tokenClient,
                             BizUserRepository bizUserRepository,
                             OAuthUserLinkRepository linkRepository) {
        this.tokenClient = tokenClient;
        this.bizUserRepository = bizUserRepository;
        this.linkRepository = linkRepository;
    }

    public OAuthLoginResult handleCallback(String code) {
        TokenResponse token = tokenClient.exchangeCode(code);
        JsonNode userinfo = unwrapUserinfo(tokenClient.fetchUserinfo(token.accessToken()));
        String loginId = firstNonNull(
                text(userinfo, "loginId"),
                text(userinfo, "sub"),
                token.openid());
        String nickname = firstNonNull(text(userinfo, "nickname"), loginId);

        Optional<OAuthUserLink> existing = linkRepository.findByOAuthLoginId(loginId);
        if (existing.isPresent()) {
            BizUser biz = bizUserRepository.findById(existing.get().bizUserId()).orElseThrow();
            return new OAuthLoginResult.LoggedIn(new OAuthUserSession(
                    loginId, token.openid(), token.accessToken(), nickname, biz.id()));
        }

        Optional<BizUser> sameName = bizUserRepository.findByUsername(loginId);
        if (sameName.isPresent()) {
            linkRepository.save(new OAuthUserLink(loginId, sameName.get().id()));
            BizUser biz = sameName.get();
            return new OAuthLoginResult.LoggedIn(new OAuthUserSession(
                    loginId, token.openid(), token.accessToken(), nickname, biz.id()));
        }

        return new OAuthLoginResult.NeedBind(loginId, token.openid(), token.accessToken(), nickname);
    }

    public OAuthUserSession bind(String oauthLoginId, String openid, String accessToken,
                                 String nickname, String localUsername, String localPassword) {
        BizUser biz = bizUserRepository.findByUsername(localUsername)
                .filter(u -> u.password().equals(localPassword))
                .orElseThrow(() -> new IllegalArgumentException("本地账号或密码错误"));
        linkRepository.save(new OAuthUserLink(oauthLoginId, biz.id()));
        return new OAuthUserSession(oauthLoginId, openid, accessToken, nickname, biz.id());
    }

    private static JsonNode unwrapUserinfo(JsonNode data) {
        if (data.has("userinfo") && data.path("userinfo").isObject()) {
            return data.path("userinfo");
        }
        return data;
    }

    private static String text(JsonNode node, String field) {
        JsonNode v = node.path(field);
        return v.isMissingNode() || v.isNull() ? null : v.asText();
    }

    private static String firstNonNull(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return null;
    }

    public sealed interface OAuthLoginResult permits OAuthLoginResult.LoggedIn, OAuthLoginResult.NeedBind {
        record LoggedIn(OAuthUserSession session) implements OAuthLoginResult {
        }

        record NeedBind(String loginId, String openid, String accessToken, String nickname)
                implements OAuthLoginResult {
        }
    }
}
