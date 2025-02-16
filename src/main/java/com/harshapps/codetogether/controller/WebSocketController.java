package com.harshapps.codetogether.controller;

import com.harshapps.codetogether.service.DynamicWebSocketRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for Web Sockets
 */
@RestController
@Tag(name = "WebSocket Controller", description = "Exposes Endpoint for Web Sockets")
public class WebSocketController {

    private final DynamicWebSocketRegistry dynamicWebSocketRegistry;

    /**
     * Creates new WebSocketController
     * @param dynamicWebSocketRegistry A Dynamic WebSocket Registry for creating sockets dunamically
     */
    public WebSocketController(DynamicWebSocketRegistry dynamicWebSocketRegistry) {
        this.dynamicWebSocketRegistry = dynamicWebSocketRegistry;
    }

    private static final Logger logger = LogManager.getLogger(DynamicWebSocketRegistry.class);

    /**
     * Creates Socket for Code
     * @param socketName CodeSocket Name
     * @return Success/Failure Message
     */
    @GetMapping("/codesocket/{socketName}")
    public ResponseEntity<String> createCodeSocket(@PathVariable String socketName) {
        logger.info("Invoking createCodeSocket for socket:{}",socketName);
        boolean isRegistered = dynamicWebSocketRegistry.registerCodeEndpoint(socketName);
        if (isRegistered) {
            return ResponseEntity.ok("WebSocket endpoint '/" + socketName + "' created successfully.");
        } else {
            return ResponseEntity.ok("WebSocket endpoint '/" + socketName + "' already exists.");
        }
    }
    /**
     * Creates Socket for Draw
     * @param socketName drawSocket Name
     * @return Success/Failure Message
     */
    @GetMapping("/drawsocket/{socketName}")
    @Operation(summary = "Create a webSocket for code", description = "Create a web socket based on socket name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<String> createDrawSocket(@PathVariable @Parameter(description = "Name for socket") String socketName) {
        logger.info("Invoking createDrawSocket for socket:{}",socketName);
        boolean isRegistered = dynamicWebSocketRegistry.registerDrawEndpoint(socketName);
        if (isRegistered) {
            return ResponseEntity.ok("WebSocket endpoint '/" + socketName + "' created successfully.");
        } else {
            return ResponseEntity.ok("WebSocket endpoint '/" + socketName + "' already exists.");
        }
    }
}
