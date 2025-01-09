package com.harshapps.codetogether.controller;

import com.harshapps.codetogether.service.DynamicWebSocketRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {

    private final DynamicWebSocketRegistry dynamicWebSocketRegistry;

    public WebSocketController(DynamicWebSocketRegistry dynamicWebSocketRegistry) {
        this.dynamicWebSocketRegistry = dynamicWebSocketRegistry;
    }

    @GetMapping("/socket/{socketName}")
    public ResponseEntity<String> createSocket(@PathVariable String socketName) {
        boolean isRegistered = dynamicWebSocketRegistry.registerEndpoint(socketName);
        if (isRegistered) {
            return ResponseEntity.ok("WebSocket endpoint '/" + socketName + "' created successfully.");
        } else {
            return ResponseEntity.ok("WebSocket endpoint '/" + socketName + "' already exists.");
        }
    }
}
