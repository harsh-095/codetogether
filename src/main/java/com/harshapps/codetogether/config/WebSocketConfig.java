package com.harshapps.codetogether.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.socket.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final Map<String, WebSocketHandler> socketHandlers = new ConcurrentHashMap<>();

    @Override
    @Scheduled(fixedDelayString = "1",initialDelayString = "1")
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        System.out.println("Registeringgg----");
        for(int i = 0; i<100; i++) {
            registry.addHandler(new MyWebSocketHandler(i+""), "/{socketName}/"+i)
                    .setAllowedOrigins("*"); // Adjust allowed origins as needed
        }


    }

    public void registerSocketHandler(String socketName) {
        socketHandlers.put(socketName, new MyWebSocketHandler(""));
    }
}

class MyWebSocketHandler extends AbstractWebSocketHandler {
    private String index;
    MyWebSocketHandler(String index){
        this.index= index;
    }
    List<WebSocketSession> webSocketSessions
            = Collections.synchronizedList(new ArrayList<>());
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Connection established: " + session.getId());
        webSocketSessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
//        if(!session.getUri().getPath().substring(session.getUri().getPath().lastIndexOf("/")+1).equals(index)){
//            return;
//        }
        System.out.println("Received message: " + payload);
//        System.out.println("Session"+session.getId());
//        System.out.println("Session"+session.getId());

        // Send the message to all connected clients in the same socket
        for (WebSocketSession otherSession : webSocketSessions) {
            if (!otherSession.equals(session)) {
                otherSession.sendMessage(message);
            }
        }
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus status)throws Exception{
        System.out.println("Connection Disconnected: " + session.getId());
        webSocketSessions.remove(session);
    }
}
