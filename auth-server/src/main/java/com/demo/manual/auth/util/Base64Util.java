package com.demo.manual.auth.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class Base64Util {

    private Base64Util() {
    }

    public static String decodeBasicAuth(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            return null;
        }
        String encoded = authorizationHeader.substring(6).trim();
        return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    }
}
