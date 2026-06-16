package com.demo.bff.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserInfo(
        String sub,
        @JsonProperty("preferred_username") String username,
        String name
) {
}
