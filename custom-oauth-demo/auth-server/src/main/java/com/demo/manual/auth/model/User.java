package com.demo.manual.auth.model;

/** 授权中心统一账号（认证用；各业务系统用户由子系统自行维护） */
public record User(
        Long id,
        String username,
        String password,
        String nickname
) {
}
