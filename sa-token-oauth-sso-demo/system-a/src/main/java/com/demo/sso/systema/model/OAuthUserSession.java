package com.demo.sso.systema.model;

import java.io.Serializable;

public record OAuthUserSession(
        String loginId,
        String openid,
        String accessToken,
        String nickname
) implements Serializable {
}
