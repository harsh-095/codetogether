package com.harshapps.codetogether.controller;

import com.harshapps.codetogether.handler.SocketConnectionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EnableWebSocket
public class DynamicWebSocketController implements WebSocketConfigurer {
//
//    // Store dynamic WebSocket handlers
//    private final Map<String, WebSocketHandler> socketHandlers = new ConcurrentHashMap<>();
//
//    private final List<String> handlers = new ArrayList<>();
//
//    private WebSocketHandlerRegistry handlerRegistry;
//
//    @Override
//    public synchronized void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        this.handlerRegistry = registry;
//        handlers.forEach(handler->{
//            handlerRegistry
//                    .addHandler(new SocketConnectionHandler(),handler)
//                    .setAllowedOrigins("*");
//        });
//    }
//
//    /**
//     * Dynamically registers a WebSocket endpoint for the given socket name.
//     *
//     * @param socketName The name of the WebSocket endpoint.
//     */
//    public synchronized void registerSocket(String socketName) throws InterruptedException {
//        System.out.println("In_Socket="+socketName);
//        System.out.println("socketHandlers="+socketHandlers);
//        if (socketHandlers.containsKey(socketName)) {
//            // If socket already exists, skip creation
//            return;
//        }
//
//        WebSocketHandler handler = new SocketConnectionHandler();
////        handlerRegistry.addHandler(handler, "/" + socketName).setAllowedOrigins("*");
//        handlers.add(socketName);
//        registerWebSocketHandlers(handlerRegistry);
//        Thread.sleep(5000);
//        socketHandlers.put(socketName, handler);
//        System.out.println("socketHandlers="+socketHandlers);
//    }

//    @Bean
//    public ServerEndpointExporter serverEndpointExporter() {
//        return new ServerEndpointExporter();
//    }


    private final Map<String, WebSocketHandler> socketHandlers = new ConcurrentHashMap<>();

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketConnectionHandler(), "/chat/{socketName}")
                .setAllowedOrigins("*"); // Adjust allowed origins as needed

        // Dynamically register handlers based on socketName
        socketHandlers.forEach((socketName, handler) -> {
            registry.addHandler(handler, "/chat/" + socketName)
                    .setAllowedOrigins("*");
        });
    }

    // Method to register a new WebSocketHandler for a specific socketName
    public void registerSocketHandler(String socketName) {
        socketHandlers.put(socketName, new SocketConnectionHandler());
    }
}
