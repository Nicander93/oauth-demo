package com.demo.manual.client.model;

/** OAuth sub ↔ 本地用户 ID 的绑定关系（存于本系统） */
public record OAuthUserLink(
        String oidcSub,
        Long localUserId
) {
}
