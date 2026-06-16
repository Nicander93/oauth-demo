package com.demo.manual.client.model;

import java.io.Serializable;

/** 本系统登录态：OIDC 身份 + 已绑定的本地用户 */
public record ClientLoginSession(
        UserInfo oidcUser,
        LocalUser localUser,
        String accessToken,
        String idToken
) implements Serializable {
}
