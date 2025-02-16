package com.harshapps.codetogether.socketHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Web Socket Handler
 */
public class LogSocketHandler implements WebSocketHandler {

    private static final LogSocketHandler INSTANCE = new LogSocketHandler();

    private LogSocketHandler() {} // Private constructor

    public static LogSocketHandler getInstance() {
        return INSTANCE;
    }

    private static final Logger logger = LogManager.getLogger(LogSocketHandler.class);

    private final List<WebSocketSession> webSocketSessions = Collections.synchronizedList(new ArrayList<>());

    /**
     * Function to be executed after Session is connected
     * @param session Refers to Session which is connected
     * @throws IOException Exceptions while sending messages
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        webSocketSessions.add(session);
        logger.info("Log Session: {} Connected", session.getId());
    }

    /**
     * Function to be executed after Session is disconnected
     * @param session Refers to Session which is disconnected
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        webSocketSessions.remove(session);
        logger.info("Log Session: {} Disconnected", session.getId());
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
     * @param msg The message to be sent
     */
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
        logger.error("Transport error: {}", exception.getMessage());
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }
}

