package com.demo.bff.model;

public record LocalUser(
        Long id,
        String userCode,
        String username,
        String password,
        String nickname
) {
}
