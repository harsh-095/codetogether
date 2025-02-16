package com.harshapps.codetogether.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for UI Pages using thymeleaf
 */
@Controller
@Tag(name = "Frontend Controller", description = "Exposes Endpoint for Frontend Pages")
public class FrontendController {

    /**
     * Redirects to index Page
     * @return "index"
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

//    @GetMapping("/test")
//    public String test() {
//        return "test";
//    }

    /**
     * Redirects to home Page
     * @return "index"
     */
    @GetMapping("/home")
    public String home() {
        return "index";
    }

    /**
     * Redirects to log Page
     * @return log
     */
    @GetMapping("/log")
    @Operation(summary = "Live Log Pages", description = "Pages to fetch all live logs")
    public String log() {
        return "log";
    }
//
//    @GetMapping("/code")
//    public String code() {
//        return "code";
//    }
//
//    @GetMapping("/chat")
//    public String chat() {
//        return "chat";
//    }

    /**
     * Redirects to draw Page
     * @return "draw"
     */
    @GetMapping("/draw")
    public String draw() {
        return "draw";
    }
}

