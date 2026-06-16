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

/**
 * OAuth 回调后的本地用户解析：先查 sub 绑定，再尝试用户名自动绑定。
 */
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

        log.warn("无匹配的本地用户: sub={}, username={}", sub, userInfo.username());
        return new OAuthLoginResult.NoLocalUser(sub, userInfo.username());
    }

    private OAuthLoginResult.LoggedIn loggedIn(TokenResponse token, UserInfo userInfo, LocalUser local) {
        return new OAuthLoginResult.LoggedIn(new ClientLoginSession(
                userInfo, local, token.accessToken(), token.idToken()));
    }

    public sealed interface OAuthLoginResult permits OAuthLoginResult.LoggedIn, OAuthLoginResult.NoLocalUser {
        record LoggedIn(ClientLoginSession session) implements OAuthLoginResult {
        }

        record NoLocalUser(String sub, String username) implements OAuthLoginResult {
        }
    }
}
