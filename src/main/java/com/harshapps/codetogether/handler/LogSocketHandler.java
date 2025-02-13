package com.harshapps.codetogether.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class LogSocketHandler implements WebSocketHandler {

    private static final LogSocketHandler INSTANCE = new LogSocketHandler();

    private LogSocketHandler() {} // Private constructor

    public static LogSocketHandler getInstance() {
        return INSTANCE;
    }

    private final List<WebSocketSession> webSocketSessions = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        System.out.println(session.getId() + " Connected");
        webSocketSessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println(session.getId() + " Disconnected");
        webSocketSessions.remove(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    }

    public void send(String msg) {
        for(WebSocketSession session:webSocketSessions) {
            try {
                session.sendMessage(new TextMessage(msg));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("Transport error: " + exception.getMessage());
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }
}

