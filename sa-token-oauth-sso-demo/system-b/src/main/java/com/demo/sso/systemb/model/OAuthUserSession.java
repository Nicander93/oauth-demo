package com.demo.sso.systemb.model;

import java.io.Serializable;

public record OAuthUserSession(
        String loginId,
        String openid,
        String accessToken,
        String nickname,
        Long bizUserId
) implements Serializable {
}
