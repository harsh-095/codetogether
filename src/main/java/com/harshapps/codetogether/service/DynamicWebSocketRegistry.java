package com.harshapps.codetogether.service;

import com.harshapps.codetogether.handler.CodeSocketConnectionHandler;
import com.harshapps.codetogether.handler.DrawSocketConnectionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    public DynamicWebSocketRegistry(SimpleUrlHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    /**
     * Creates websockets dynamically
     *
     * @param socketName name of the socket
     * @return boolean type whether registered or not.
     */
    public synchronized boolean registerCodeEndpoint(String socketName) {
        if (handlers.containsKey(socketName)) {
            return false; // Endpoint already exists
        }

        WebSocketHandler handler = new CodeSocketConnectionHandler();
        WebSocketHttpRequestHandler requestHandler = new WebSocketHttpRequestHandler(handler, new DefaultHandshakeHandler());

        handlers.put(socketName, requestHandler);

        // Dynamically add the endpoint to the handlerMapping
        Map<String, Object> urlMap = (Map<String, Object>) handlerMapping.getUrlMap();
        urlMap.put("/code/" + socketName, requestHandler);
        handlerMapping.setUrlMap(urlMap);
        handlerMapping.initApplicationContext(); // Reinitialize to apply changes

        return true;
    }

    public synchronized boolean registerDrawEndpoint(String socketName) {
        if (handlers.containsKey(socketName)) {
            return false; // Endpoint already exists
        }

        WebSocketHandler handler = new DrawSocketConnectionHandler();
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
