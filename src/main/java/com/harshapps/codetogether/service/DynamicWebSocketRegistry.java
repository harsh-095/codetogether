package com.harshapps.codetogether.service;

import com.harshapps.codetogether.handler.SocketConnectionHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.HashMap;
import java.util.Map;

@Service
public class DynamicWebSocketRegistry {

    private final Map<String, WebSocketHttpRequestHandler> handlers = new HashMap<>();
    private final SimpleUrlHandlerMapping handlerMapping;

    public DynamicWebSocketRegistry(SimpleUrlHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    public synchronized boolean registerEndpoint(String socketName) {
        if (handlers.containsKey(socketName)) {
            return false; // Endpoint already exists
        }

        WebSocketHandler handler = new SocketConnectionHandler();
        WebSocketHttpRequestHandler requestHandler = new WebSocketHttpRequestHandler(handler, new DefaultHandshakeHandler());

        handlers.put(socketName, requestHandler);

        // Dynamically add the endpoint to the handlerMapping
        Map<String, Object> urlMap = (Map<String, Object>) handlerMapping.getUrlMap();
        urlMap.put("/chat/" + socketName, requestHandler);
        handlerMapping.setUrlMap(urlMap);
        handlerMapping.initApplicationContext(); // Reinitialize to apply changes

        return true;
    }
}
