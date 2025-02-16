package com.harshapps.codetogether.config;

import com.harshapps.codetogether.socketHandler.LogSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.HashMap;

/**
 * Configuration class for web sockets
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    /**
     * Log Web Socket Handler
     */
    private final LogSocketHandler logSocketHandler = LogSocketHandler.getInstance();

    /**
     * To Register Web Socket Handlers with a path
     * @param registry Web Socket Registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(logSocketHandler, "/logs").setAllowedOrigins("*");
    }

    /**
     * Bean for SimpleUrlHandlerMapping
     * @return SimpleUrlHandlerMapping
     */
    @Bean
    public SimpleUrlHandlerMapping simpleUrlHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(1); // Define the order of this handler
        mapping.setUrlMap(new HashMap<>()); // Start with an empty map
        return mapping;
    }
}
