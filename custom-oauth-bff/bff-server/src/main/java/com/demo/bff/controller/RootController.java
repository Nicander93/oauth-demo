package com.demo.bff.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @Value("${manual.bff.ui-base-url:http://localhost:5173}")
    private String uiBaseUrl;

    @GetMapping("/")
    public String root() {
        return "redirect:" + uiBaseUrl;
    }
}
