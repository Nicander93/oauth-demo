package com.demo.manual.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** GET /userinfo 返回的 OIDC 标准声明（不含业务系统字段） */
public record UserInfo(
        String sub,
        @JsonProperty("preferred_username") String username,
        String name
) {
}
