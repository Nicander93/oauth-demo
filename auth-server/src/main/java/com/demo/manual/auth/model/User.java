package com.demo.manual.auth.model;

public record User(
        Long id,
        String username,
        String password,
        String nickname
) {
}
