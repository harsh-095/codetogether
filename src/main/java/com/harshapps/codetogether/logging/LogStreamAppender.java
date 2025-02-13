package com.harshapps.codetogether.logging;


import com.harshapps.codetogether.handler.LogSocketHandler;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//@Component
@Plugin(name = "Appender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class LogStreamAppender extends AbstractAppender {


    private final LogSocketHandler logSocketHandler= LogSocketHandler.getInstance();

    private static final LinkedBlockingQueue<String> logQueue = new LinkedBlockingQueue<>(1000); // Max size 1000

    protected LogStreamAppender(String name, Layout<? extends Serializable> layout, Filter filter) {
        super(name, filter, layout);
    }

    @Override
    public void append(LogEvent event) {
        // Convert the log event to a string and add it to the queue
        String logMessage = this.getLayout().toSerializable(event).toString();
        try {
            logQueue.put(logMessage); // Add the log to the queue
            System.out.println("logMessage="+logMessage);
            logSocketHandler.send(logMessage);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static LinkedBlockingQueue<String> getLogQueue() {
        return logQueue;
    }
//
//    public static Appender createAppender() {
//        Layout<?> layout = PatternLayout.createDefaultLayout();
//        return new LogStreamAppender("LogStreamAppender", layout);
//    }
@PluginFactory
public static LogStreamAppender createAppender(@PluginAttribute("name") String name,
                                               @PluginElement("Layout") Layout<? extends Serializable> layout,
                                               @PluginElement("Filters") Filter filter) {
    // Default layout if not provided
    if (layout == null) {
        layout = PatternLayout.createDefaultLayout();
    }
    return new LogStreamAppender(name, layout, filter);
}
}