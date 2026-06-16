package com.demo.manual.client.model;

/** 系统 A 本地业务用户 */
public record LocalUser(
        Long id,
        String userCode,
        String username,
        String password,
        String nickname
) {
}
