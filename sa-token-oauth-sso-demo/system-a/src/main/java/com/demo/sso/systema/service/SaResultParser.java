package com.demo.sso.systema.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class SaResultParser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SaResultParser() {
    }

    public static JsonNode parse(String body) {
        try {
            JsonNode root = MAPPER.readTree(body);
            int code = root.path("code").asInt(-1);
            if (code != 200) {
                String msg = root.path("msg").asText("请求失败");
                throw new IllegalStateException(msg);
            }
            return root.path("data");
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("解析响应失败: " + e.getMessage(), e);
        }
    }
}
