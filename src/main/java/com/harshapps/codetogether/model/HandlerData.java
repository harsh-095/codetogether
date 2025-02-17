package com.harshapps.codetogether.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HandlerData {
    private Map<String, SessionData> drawHandlers;
    private Map<String, SessionData> codeHandlers;
    private SessionData logSessionCount;

    public void addLogSession(){
        logSessionCount.addSession();
    }

    public void removeLogSession(){
        logSessionCount.removeSession();
    }

    public void addDrawSession(String socketName) {
        drawHandlers.getOrDefault(socketName, new SessionData(0)).addSession();
    }

    public void addCodeSession(String socketName) {
        codeHandlers.getOrDefault(socketName, new SessionData(0)).addSession();
    }

    public void removeDrawSession(String socketName) {
        drawHandlers.get(socketName).removeSession();
    }

    public void removeCodeSession(String socketName) {
        codeHandlers.get(socketName).removeSession();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class SessionData {
        private Integer activeSessionCount;

        public void addSession() {
            activeSessionCount++;
        }

        public void removeSession() {
            activeSessionCount--;
        }
    }
}
