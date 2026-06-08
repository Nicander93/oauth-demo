package com.demo.manual.auth.model;

public record BizUser(
        Long id,
        String systemCode,
        String bizUserCode,
        String bizUsername,
        String bizNickname
) {
}
