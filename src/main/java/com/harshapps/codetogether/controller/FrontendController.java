package com.harshapps.codetogether.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/home")
    public String home() {
        return "index";
    }

    @GetMapping("/code")
    public String code() {
        return "code";
    }

    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }

    @GetMapping("/draw")
    public String draw() {
        return "draw";
    }
}

