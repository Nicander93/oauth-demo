package com.demo.bff.model;

import java.io.Serializable;

public record ClientLoginSession(
        UserInfo oidcUser,
        LocalUser localUser,
        String accessToken,
        String idToken
) implements Serializable {
}
