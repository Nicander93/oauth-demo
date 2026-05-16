package com.demo.oauth.client.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;

@Controller
public class HomeController {

    @Value("${demo.resource.userinfo-uri}")
    private String userinfoUri;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String home(
            @RegisteredOAuth2AuthorizedClient("demo-client") OAuth2AuthorizedClient authorizedClient,
            @AuthenticationPrincipal OAuth2User oauth2User,
            Model model) {
        model.addAttribute("username", oauth2User != null ? oauth2User.getName() : "");
        model.addAttribute("attributes", oauth2User != null ? oauth2User.getAttributes() : null);
        if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
            model.addAttribute("accessToken", authorizedClient.getAccessToken().getTokenValue());
        }
        String bearer = authorizedClient != null && authorizedClient.getAccessToken() != null
                ? authorizedClient.getAccessToken().getTokenValue()
                : null;
        if (bearer != null) {
            String json = RestClient.create().get()
                    .uri(userinfoUri)
                    .headers(h -> h.setBearerAuth(bearer))
                    .retrieve()
                    .body(String.class);
            model.addAttribute("userinfoJson", json);
        }
        return "home";
    }
}
