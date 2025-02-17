package com.harshapps.codetogether.service;

import com.harshapps.codetogether.model.HandlerData;
import com.harshapps.codetogether.socketHandler.AdminSocketHandler;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;


@Service
@Data
public class AdminService {
    private HandlerData handlerData = new HandlerData(new HashMap<>(), new HashMap<>(), new HandlerData.SessionData(0));

    private AdminSocketHandler adminSocketHandler = AdminSocketHandler.getInstance();

    public void addDrawHandler(String socketName) {
        handlerData.addDrawSession(socketName);
        sendHandlerDataToSocket();
    }

    public void addCodeHandler(String socketName) {
        handlerData.addCodeSession(socketName);
        sendHandlerDataToSocket();
    }

    public void removeDrawHandler(String socketName) {
        handlerData.removeDrawSession(socketName);
        sendHandlerDataToSocket();
    }

    public void removeCodeHandler(String socketName) {
        handlerData.removeCodeSession(socketName);
        sendHandlerDataToSocket();
    }

    public void addLogSession(){
        handlerData.addLogSession();
        sendHandlerDataToSocket();
    }

    public void removeLogSession(){
        handlerData.removeLogSession();
        sendHandlerDataToSocket();
    }

    private void sendHandlerDataToSocket(){
        try {
            adminSocketHandler.send(handlerData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
