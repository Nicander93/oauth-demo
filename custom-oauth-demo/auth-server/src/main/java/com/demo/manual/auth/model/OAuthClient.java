package com.demo.manual.auth.model;

import java.util.Set;

/** OAuth 2.0 注册客户端；systemCode 用于关联业务系统用户 */
public record OAuthClient(
        String clientId,
        String clientSecret,
        Set<String> redirectUris,  // 允许的回调地址白名单
        Set<String> scopes,        // 该客户端可申请的 scope
        String systemCode          // 对应业务系统标识，UserInfo 扩展用
) {
}
