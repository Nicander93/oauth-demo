package com.demo.manual.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** GET /userinfo 返回的用户声明；biz_* 为按业务系统扩展的字段 */
public record UserInfo(
        String sub,
        @JsonProperty("preferred_username") String username,
        String name,
        @JsonProperty("biz_user_id") String bizUserId,
        @JsonProperty("biz_username") String bizUsername,
        @JsonProperty("biz_nickname") String bizNickname
) {
}
