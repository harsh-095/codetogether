package com.harshapps.codetogether.socketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harshapps.codetogether.model.HandlerData;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Web Socket Handler
 */
public class AdminSocketHandler implements WebSocketHandler {

    private static final AdminSocketHandler INSTANCE = new AdminSocketHandler();

    private AdminSocketHandler() {} // Private constructor

    public static AdminSocketHandler getInstance() {
        return INSTANCE;
    }

    private static final Logger logger = LogManager.getLogger(AdminSocketHandler.class);

    private final List<WebSocketSession> webSocketSessions = Collections.synchronizedList(new ArrayList<>());

    private HandlerData currentHandlerData = new HandlerData(new HashMap<>(), new HashMap<>(), new HandlerData.SessionData(0));

    /**
     * Function to be executed after Session is connected
     * @param session Refers to Session which is connected
     * @throws IOException Exceptions while sending messages
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        webSocketSessions.add(session);
        send(session,currentHandlerData);
        logger.info("Admin Session: {} Connected", session.getId());
    }

    /**
     * Function to be executed after Session is disconnected
     * @param session Refers to Session which is disconnected
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        webSocketSessions.remove(session);
        logger.info("Admin Session: {} Disconnected", session.getId());
    }

    /**
     * Handles Messages in sessions
     * @param session Refers to Session which is receives message
     * @throws IOException Exceptions while sending messages
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    }

    /**
     * To send message to the session
     * @param handlerData The message to be sent
     */
    public void send(HandlerData handlerData) throws IOException {
        currentHandlerData = handlerData;
        for(WebSocketSession session:webSocketSessions) {
            send(session, handlerData);
        }
    }

    public void send(WebSocketSession session, HandlerData handlerData) throws IOException {
        currentHandlerData = handlerData;
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(handlerData);
        session.sendMessage(new TextMessage(json));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        logger.error("Transport error: {}", exception.getMessage());
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }
}

