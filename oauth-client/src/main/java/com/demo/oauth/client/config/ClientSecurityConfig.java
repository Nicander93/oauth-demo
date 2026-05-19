package com.demo.oauth.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ClientSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 客户端应用的安全规则：
        // 1) 首页/错误页放行
        // 2) 其他请求都需要先登录
        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/", "/error", "/css/**").permitAll()
                        .anyRequest().authenticated())
                // 开启 OAuth2 登录（授权码模式）
                // 登录成功后统一跳转到 /home
                .oauth2Login((oauth2) -> oauth2.defaultSuccessUrl("/home", true))
                // 使用 Spring Security 默认登出端点 /logout
                .logout(Customizer.withDefaults());
        return http.build();
    }
}
