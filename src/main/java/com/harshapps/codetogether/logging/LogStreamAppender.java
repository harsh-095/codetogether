package com.harshapps.codetogether.logging;


import com.harshapps.codetogether.socketHandler.LogSocketHandler;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;


/**
 *  A Log Appender Class used to Catch logs in console and append to log socket handler.(Used in log4j2.xml)
 */
@Plugin(name = "Appender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class LogStreamAppender extends AbstractAppender {


    private final LogSocketHandler logSocketHandler= LogSocketHandler.getInstance();

    protected LogStreamAppender(String name, Layout<? extends Serializable> layout, Filter filter) {
        super(name, filter, layout);
    }

    /**
     * Function that gets called when something is logged.(Used in log4j2.xml)
     * @param event The LogEvent.
     */
    @Override
    public void append(LogEvent event) {
        String logMessage = this.getLayout().toSerializable(event).toString();
        try {
            logSocketHandler.send(logMessage);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Used to create an Appender
     * @param name LogAppender name
     * @param layout A Pattern Layout
     * @param filter A Threshold Filter
     * @return Returns LogStreamAppender
     */
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