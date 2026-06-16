package com.demo.manual.client.service;

import com.demo.manual.client.model.ClientLoginSession;
import com.demo.manual.client.model.LocalUser;
import com.demo.manual.client.model.OAuthUserLink;
import com.demo.manual.client.model.TokenResponse;
import com.demo.manual.client.model.UserInfo;
import com.demo.manual.client.repository.LocalUserRepository;
import com.demo.manual.client.repository.OAuthUserLinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OAuthLoginService {

    private static final Logger log = LoggerFactory.getLogger(OAuthLoginService.class);

    private final OAuthUserLinkRepository linkRepository;
    private final LocalUserRepository localUserRepository;

    public OAuthLoginService(OAuthUserLinkRepository linkRepository,
                             LocalUserRepository localUserRepository) {
        this.linkRepository = linkRepository;
        this.localUserRepository = localUserRepository;
    }

    public OAuthLoginResult handleCallback(TokenResponse token, UserInfo userInfo) {
        String sub = userInfo.sub();

        Optional<OAuthUserLink> existing = linkRepository.findByOidcSub(sub);
        if (existing.isPresent()) {
            LocalUser local = localUserRepository.findById(existing.get().localUserId()).orElseThrow();
            log.info("sub 已绑定本地用户: sub={} -> {}", sub, local.userCode());
            return loggedIn(token, userInfo, local);
        }

        Optional<LocalUser> sameName = localUserRepository.findByUsername(userInfo.username());
        if (sameName.isPresent()) {
            linkRepository.save(new OAuthUserLink(sub, sameName.get().id()));
            log.info("用户名相同，自动绑定: sub={} -> {}", sub, sameName.get().userCode());
            return loggedIn(token, userInfo, sameName.get());
        }

        log.info("需手动绑定本地账号: sub={}, username={}", sub, userInfo.username());
        return new OAuthLoginResult.NeedBind(sub, userInfo, token.accessToken(), token.idToken());
    }

    public ClientLoginSession bind(String oidcSub, UserInfo userInfo,
                                   String accessToken, String idToken,
                                   String localUsername, String localPassword) {
        LocalUser local = localUserRepository.findByUsername(localUsername)
                .filter(u -> u.password().equals(localPassword))
                .orElseThrow(() -> new IllegalArgumentException("本地账号或密码错误"));
        linkRepository.save(new OAuthUserLink(oidcSub, local.id()));
        log.info("手动绑定成功: sub={} -> {}", oidcSub, local.userCode());
        return new ClientLoginSession(userInfo, local, accessToken, idToken);
    }

    private OAuthLoginResult.LoggedIn loggedIn(TokenResponse token, UserInfo userInfo, LocalUser local) {
        return new OAuthLoginResult.LoggedIn(new ClientLoginSession(
                userInfo, local, token.accessToken(), token.idToken()));
    }

    public sealed interface OAuthLoginResult permits OAuthLoginResult.LoggedIn, OAuthLoginResult.NeedBind {
        record LoggedIn(ClientLoginSession session) implements OAuthLoginResult {
        }

        record NeedBind(String sub, UserInfo userInfo, String accessToken, String idToken) implements OAuthLoginResult {
        }
    }
}
