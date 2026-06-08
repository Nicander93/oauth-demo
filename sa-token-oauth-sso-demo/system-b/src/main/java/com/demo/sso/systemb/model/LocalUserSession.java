package com.demo.sso.systemb.model;

import java.io.Serializable;

public record LocalUserSession(Long bizUserId, String username, String nickname) implements Serializable {
}
