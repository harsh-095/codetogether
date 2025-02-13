package com.harshapps.codetogether.controller;

import com.harshapps.codetogether.handler.LogSocketHandler;
import com.harshapps.codetogether.logging.LogStreamAppender;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.BlockingQueue;

//@Component
//@ServerEndpoint("/logs")
public class LogController {
//
//    @Autowired
//    private LogSocketHandler logSocketHandler;

    private final BlockingQueue<String> logQueue = LogStreamAppender.getLogQueue();

//    // Logger for the LogController
//    private static final Logger logger = LogManager.getLogger(LogController.class);


    // SSE endpoint to stream logs
    @GetMapping("/logs")
    public SseEmitter streamLogs() {
        SseEmitter emitter = new SseEmitter();

        // Separate thread to handle log streaming to the client
        new Thread(() -> {
            try {
                while (true) {
                    String logMessage = logQueue.poll();  // Poll a log message from the queue
                    if (logMessage != null) {
                        emitter.send(logMessage);  // Send the log message to the client
                    }
                    else{
//                        emitter.send("");
                        Thread.sleep(1000);
                    } // Poll every 500ms for new log messages
                }
            } catch (Exception e) {
                emitter.completeWithError(e);  // Complete with error in case of issues
            }
        }).start();

        return emitter;  // Return the emitter to the client
    }
}

