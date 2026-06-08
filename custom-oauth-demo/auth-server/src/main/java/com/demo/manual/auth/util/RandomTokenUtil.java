package com.demo.manual.auth.util;

import java.security.SecureRandom;
import java.util.Base64;

public final class RandomTokenUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private RandomTokenUtil() {
    }

    public static String randomToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
