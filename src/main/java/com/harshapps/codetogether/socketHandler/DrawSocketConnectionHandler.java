package com.harshapps.codetogether.socketHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harshapps.codetogether.service.AdminService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Draw Socket Handler
 */
public class DrawSocketConnectionHandler implements WebSocketHandler {

    private final List<WebSocketSession> webSocketSessions = Collections.synchronizedList(new ArrayList<>());
    private WebSocketSession primarySession = null;
    private WebSocketMessage<?> currentMessage = null;
    private static final Logger logger = LogManager.getLogger(DrawSocketConnectionHandler.class);
    private final String socketName;
    private final AdminService adminService;

    public DrawSocketConnectionHandler(String socketName, AdminService adminService){
        this.socketName = socketName;
        this.adminService = adminService;
    }

    /**
     * Function to be executed after Session is connected
     * @param session Refers to Session which is connected
     * @throws IOException Exceptions while sending messages
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        logger.info("DrawSession: {} Connected for SocketName: {}", session.getId(), socketName);
        webSocketSessions.add(session);
        adminService.addDrawHandler(socketName);
        // Assign the first session as the primary session
        if (primarySession == null) {
            primarySession = session;
            sendToSession(session, Map.of("type", "SetPrimary"));
            logger.info("DrawSession: {} is set as PrimarySession for SocketName: {}", session.getId(), socketName);
        }
        else{
            if(currentMessage!=null) {
                session.sendMessage(new TextMessage((String)currentMessage.getPayload()));
            }
        }
    }

    /**
     * Function to be executed after Session is disconnected
     * @param session Refers to Session which is disconnected
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        logger.info("DrawSession: {} Disconnected for SocketName: {}", session.getId(), socketName);
        webSocketSessions.remove(session);
        adminService.removeDrawHandler(socketName);
        // Handle primary session disconnection
        if (primarySession == session) {
            if (!webSocketSessions.isEmpty()) {
                // Assign the next session as the primary session
                primarySession = webSocketSessions.get(0);
                sendToSession(primarySession, Map.of("type", "SetPrimary"));
                logger.info("PrimarySession:{} closed ; DrawSession: {} is set as new PrimarySession for SocketName: {}", session.getId(), primarySession.getId(), socketName);
            } else {
                primarySession = null;
                currentMessage = null;// No sessions left
                logger.info("PrimarySession:{} closed ; No sessions left to set as new PrimarySession for SocketName: {}", session.getId(), socketName);
            }
        }
    }

    /**
     * Handles Messages in sessions
     * @param session Refers to Session which is receives message
     * @throws IOException Exceptions while sending messages
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = (String) message.getPayload();
        Map<String, Object> data = new ObjectMapper().readValue(payload, new TypeReference<>() {});
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

    /**
     * To send message to the session
     * @param session Session to which msg has to be sent
     * @param data The message to be sent
     */
    private void sendToSession(WebSocketSession session, Map<String, Object> data) {
        try {
            session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(data)));
        } catch (Exception e) {
            logger.error("Error occurred while sending message to code session: {} ; error: {} for SocketName: {}",session,e, socketName,e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        logger.error("Error occurred while sending message to code session: {} ; error: {} for SocketName: {}",session,exception, socketName,exception);
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }
}
