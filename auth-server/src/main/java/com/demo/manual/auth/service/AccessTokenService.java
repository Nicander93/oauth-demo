package com.demo.manual.auth.service;

import com.demo.manual.auth.model.AccessToken;
import com.demo.manual.auth.model.User;
import com.demo.manual.auth.repository.TokenRepository;
import com.demo.manual.auth.util.RandomTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccessTokenService {

    private static final Logger log = LoggerFactory.getLogger(AccessTokenService.class);

    private final TokenRepository tokenRepository;
    private final UserService userService;
    private final IdTokenService idTokenService;
    private final long tokenExpiresSeconds;

    public AccessTokenService(TokenRepository tokenRepository,
                              UserService userService,
                              IdTokenService idTokenService,
                              @Value("${manual.oauth.token-expires-seconds:3600}") long tokenExpiresSeconds) {
        this.tokenRepository = tokenRepository;
        this.userService = userService;
        this.idTokenService = idTokenService;
        this.tokenExpiresSeconds = tokenExpiresSeconds;
    }

    public AccessToken createToken(String clientId, Long authUserId, String username, Set<String> scopes) {
        String token = RandomTokenUtil.randomToken();
        AccessToken accessToken = new AccessToken(
                token,
                clientId,
                authUserId,
                username,
                scopes,
                Instant.now().plusSeconds(tokenExpiresSeconds)
        );
        tokenRepository.save(accessToken);
        log.info("生成 access_token: username={}, scopes={}", username, scopes);
        return accessToken;
    }

    public Optional<AccessToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public boolean isExpired(AccessToken token) {
        return Instant.now().isAfter(token.expiresAt());
    }

    public Map<String, Object> toTokenResponse(AccessToken token) {
        long expiresIn = token.expiresAt().getEpochSecond() - Instant.now().getEpochSecond();
        String scope = token.scopes().stream().collect(Collectors.joining(" "));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("access_token", token.token());
        response.put("token_type", "Bearer");
        response.put("expires_in", Math.max(expiresIn, 0));
        response.put("scope", scope);

        if (token.scopes().contains("openid")) {
            User user = userService.findById(token.authUserId())
                    .orElseThrow(() -> new IllegalStateException("授权用户不存在"));
            response.put("id_token", idTokenService.createIdToken(user, token.clientId()));
        }
        return response;
    }
}
