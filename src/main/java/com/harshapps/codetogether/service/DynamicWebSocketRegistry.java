package com.harshapps.codetogether.service;

import com.harshapps.codetogether.socketHandler.CodeSocketConnectionHandler;
import com.harshapps.codetogether.socketHandler.DrawSocketConnectionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for Dynamic Web Socket Registry
 */
@Service
public class DynamicWebSocketRegistry {
// TODO: Add good looging
    private final Map<String, WebSocketHttpRequestHandler> handlers = new HashMap<>();
    private final SimpleUrlHandlerMapping handlerMapping;
    private static final Logger logger = LogManager.getLogger(DynamicWebSocketRegistry.class);

    public DynamicWebSocketRegistry(SimpleUrlHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    /**
     * Creates CodedWebSockets dynamically
     *
     * @param socketName name of the CodeSocket
     * @return boolean type whether registered or not.
     */
    public synchronized boolean registerCodeEndpoint(String socketName) {
        logger.info("Invoking registerCodeEndpoint for socket:{}",socketName);
        if (handlers.containsKey(socketName)) {
            logger.info("Handler Exists for socket:{}",socketName);
            return false; // Endpoint already exists
        }
        logger.info("Creating Handler for socket:{}",socketName);
        WebSocketHandler handler = new CodeSocketConnectionHandler(socketName);
        WebSocketHttpRequestHandler requestHandler = new WebSocketHttpRequestHandler(handler, new DefaultHandshakeHandler());

        handlers.put(socketName, requestHandler);

        // Dynamically add the endpoint to the handlerMapping
        Map<String, Object> urlMap = (Map<String, Object>) handlerMapping.getUrlMap();
        urlMap.put("/code/" + socketName, requestHandler);
        handlerMapping.setUrlMap(urlMap);
        handlerMapping.initApplicationContext(); // Reinitialize to apply changes

        return true;
    }

    /**
     * Creates DrawdWebSockets dynamically
     *
     * @param socketName name of the DrawSocket
     * @return boolean type whether registered or not.
     */
    public synchronized boolean registerDrawEndpoint(String socketName) {
        logger.info("Invoking registerDrawEndpoint for socket:{}",socketName);
        if (handlers.containsKey(socketName)) {
            logger.info("Handler Exists for socket:{}",socketName);
            return false; // Endpoint already exists
        }
        logger.info("Creating Handler for socket:{}",socketName);
        WebSocketHandler handler = new DrawSocketConnectionHandler(socketName);
        WebSocketHttpRequestHandler requestHandler = new WebSocketHttpRequestHandler(handler, new DefaultHandshakeHandler());

        handlers.put(socketName, requestHandler);
        // Dynamically add the endpoint to the handlerMapping
        Map<String, Object> urlMap = (Map<String, Object>) handlerMapping.getUrlMap();
        urlMap.put("/draw/" + socketName, requestHandler);
        handlerMapping.setUrlMap(urlMap);
        handlerMapping.initApplicationContext(); // Reinitialize to apply changes

        return true;
    }
}
