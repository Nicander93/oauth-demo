package com.demo.manual.auth.service;

import com.demo.manual.auth.model.OAuthCode;
import com.demo.manual.auth.repository.CodeRepository;
import com.demo.manual.auth.util.RandomTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
/** 授权码（authorization code）的生成、校验与一次性消费 */
@Service
public class OAuthCodeService {

    private static final Logger log = LoggerFactory.getLogger(OAuthCodeService.class);

    private final CodeRepository codeRepository;
    private final long codeExpiresSeconds;

    public OAuthCodeService(CodeRepository codeRepository,
                            @Value("${manual.oauth.code-expires-seconds:300}") long codeExpiresSeconds) {
        this.codeRepository = codeRepository;
        this.codeExpiresSeconds = codeExpiresSeconds;
    }

    /** code 与 client、用户、redirect_uri、scope 绑定，换 token 时须全部匹配 */
    public OAuthCode createCode(String clientId, Long authUserId, String username,
                                String redirectUri, Set<String> scopes) {
        String code = RandomTokenUtil.randomToken();
        OAuthCode oauthCode = new OAuthCode(
                code,
                clientId,
                authUserId,
                username,
                redirectUri,
                scopes,
                Instant.now().plusSeconds(codeExpiresSeconds),
                false
        );
        codeRepository.save(oauthCode);
        log.info("生成 code: code={}, clientId={}, authUserId={}", code, clientId, authUserId);
        return oauthCode;
    }

    public Optional<OAuthCode> findByCode(String code) {
        return codeRepository.findByCode(code);
    }

    public boolean isExpired(OAuthCode code) {
        return Instant.now().isAfter(code.expiresAt());
    }

    public void markUsed(OAuthCode code) {
        OAuthCode used = new OAuthCode(
                code.code(),
                code.clientId(),
                code.authUserId(),
                code.username(),
                code.redirectUri(),
                code.scopes(),
                code.expiresAt(),
                true
        );
        codeRepository.update(used);
        log.info("code 已标记为已使用: {}", code.code());
    }

    public boolean validateForToken(OAuthCode code, String clientId, String redirectUri) {
        log.info("校验 code: code={}", code.code());
        if (code.used()) {
            log.warn("code 已使用: {}", code.code());
            return false;
        }
        if (isExpired(code)) {
            log.warn("code 已过期: {}", code.code());
            return false;
        }
        if (!code.clientId().equals(clientId)) {
            log.warn("code clientId 不匹配");
            return false;
        }
        if (!code.redirectUri().equals(redirectUri)) {
            log.warn("code redirect_uri 不匹配: expected={}, actual={}", code.redirectUri(), redirectUri);
            return false;
        }
        return true;
    }
}
