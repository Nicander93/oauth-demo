package com.demo.manual.auth.util;

import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/** 向 URL 追加查询参数（用于 authorize 回调与登录后跳回） */
public final class UrlUtil {

    private UrlUtil() {
    }

    public static String appendQuery(String baseUrl, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl);
        params.forEach(builder::queryParam);
        return builder.build().toUriString();
    }
}
