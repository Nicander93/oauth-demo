package com.demo.manual.client;

import com.demo.manual.client.config.OAuthClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/** OAuth 客户端（RP）入口，默认端口 8080：/login/oauth → 授权服务器 → /callback */
@SpringBootApplication
@EnableConfigurationProperties(OAuthClientProperties.class)
public class ClientAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientAppApplication.class, args);
    }
}
