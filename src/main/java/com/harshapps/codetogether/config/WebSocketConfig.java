package com.harshapps.codetogether.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.HashMap;

@Configuration
public class WebSocketConfig {

    @Bean
    public SimpleUrlHandlerMapping simpleUrlHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(1); // Define the order of this handler
        mapping.setUrlMap(new HashMap<>()); // Start with an empty map
        return mapping;
    }
}
