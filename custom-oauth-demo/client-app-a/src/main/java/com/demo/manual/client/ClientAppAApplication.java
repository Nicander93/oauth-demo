package com.demo.manual.client;

import com.demo.manual.client.config.OAuthClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/** OAuth 客户端 A（RP）入口，端口 8060：/login/oauth → 授权服务器 → /callback */
@SpringBootApplication
@EnableConfigurationProperties(OAuthClientProperties.class)
public class ClientAppAApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientAppAApplication.class, args);
    }
}
