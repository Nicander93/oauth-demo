package com.demo.oauth.resource.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ResourceSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 资源服务器只开放 /userinfo，且必须携带有效 access token
        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/userinfo").authenticated()
                        .anyRequest().denyAll())
                // 按 JWT 方式校验 Bearer Token（issuer/jwk 来自配置）
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
