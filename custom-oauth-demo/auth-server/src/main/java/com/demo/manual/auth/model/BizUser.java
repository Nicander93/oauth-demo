package com.demo.manual.auth.model;

/** 某业务系统内的用户，经 UserInfo 的 biz_* 字段返回给客户端 */
public record BizUser(
        Long id,
        String systemCode,
        String bizUserCode,
        String bizUsername,
        String bizNickname
) {
}
