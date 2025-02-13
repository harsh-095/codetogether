package com.harshapps.codetogether.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CodeSocketConnectionHandler implements WebSocketHandler {

    private final List<WebSocketSession> webSocketSessions = Collections.synchronizedList(new ArrayList<>());
    private WebSocketSession primarySession = null;
    private WebSocketMessage<?> currentMessage = null;
    private static final Logger logger = LogManager.getLogger(CodeSocketConnectionHandler.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        System.out.println(session.getId() + " Connected");
        logger.info("{} Connected", session.getId());
        logger.error("{} Connected", session.getId());
        logger.warn("{} Connected", session.getId());
        webSocketSessions.add(session);

        // Assign the first session as the primary session
        if (primarySession == null) {
            primarySession = session;
            sendToSession(session, Map.of("type", "SetPrimary"));
        }
        else{
            if(currentMessage!=null) {
                session.sendMessage(new TextMessage((String)currentMessage.getPayload()));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println(session.getId() + " Disconnected");
        webSocketSessions.remove(session);

        // Handle primary session disconnection
        if (primarySession == session) {
            if (!webSocketSessions.isEmpty()) {
                // Assign the next session as the primary session
                primarySession = webSocketSessions.get(0);
                sendToSession(primarySession, Map.of("type", "SetPrimary"));
            } else {
                primarySession = null;
                currentMessage = null;// No sessions left
            }
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = (String) message.getPayload();
        System.out.println("Payload="+payload);
        Map<String, Object> data = new ObjectMapper().readValue(payload, new TypeReference<>() {});

        if ("update".equals(data.get("type"))) {
            if (session == primarySession) {
                currentMessage = message;
                // Primary session sends full content to all other sessions
                for (WebSocketSession webSocketSession : webSocketSessions) {
                    if (webSocketSession != primarySession) {
                        webSocketSession.sendMessage(new TextMessage(payload));
                    }
                }
            } else {
                // Other sessions send delta to the primary session
                if (primarySession != null) {
                    primarySession.sendMessage(new TextMessage(payload));
                }
            }
        }
    }

    private void sendToSession(WebSocketSession session, Map<String, Object> data) {
        try {
            session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(data)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("Transport error: " + exception.getMessage());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
