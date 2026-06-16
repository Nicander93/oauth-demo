package com.demo.manual.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** POST /oauth/token 的 JSON 响应（蛇形字段名由 @JsonProperty 映射） */
public record TokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") long expiresIn,
        String scope,
        @JsonProperty("id_token") String idToken
) {
}
