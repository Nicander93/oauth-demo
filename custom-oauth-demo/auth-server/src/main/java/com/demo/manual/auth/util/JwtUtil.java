package com.demo.manual.auth.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/** 最小化 JWT 实现：header.payload.signature，算法 HS256（学习用，非生产库） */
public final class JwtUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JwtUtil() {
    }

    public static String createHs256(Map<String, Object> claims, String secret) {
        try {
            Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
            String headerPart = base64Url(MAPPER.writeValueAsBytes(header));
            String payloadPart = base64Url(MAPPER.writeValueAsBytes(claims));
            String content = headerPart + "." + payloadPart;
            String signature = base64Url(hmacSha256(content, secret));
            return content + "." + signature;
        } catch (Exception e) {
            throw new IllegalStateException("生成 JWT 失败", e);
        }
    }

    private static byte[] hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private static String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
