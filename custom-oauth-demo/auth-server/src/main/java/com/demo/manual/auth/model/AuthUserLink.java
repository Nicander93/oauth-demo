package com.demo.manual.auth.model;

/** auth 用户 ID ↔ 某 systemCode 下业务用户 ID 的映射 */
public record AuthUserLink(
        Long authUserId,
        String systemCode,
        Long bizUserId
) {
}
