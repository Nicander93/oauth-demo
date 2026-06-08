package com.demo.manual.auth.model;

/** 授权中心统一账号（与业务系统 BizUser 通过 AuthUserLink 关联） */
public record User(
        Long id,
        String username,
        String password,
        String nickname
) {
}
