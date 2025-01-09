package com.harshapps.codetogether.config;

//import com.harshapps.codetogether.controller.DynamicWebSocketController;
import jakarta.servlet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

//@Component
public class WebSocketFilter

//        implements Filter
{
//
//    @Autowired
//    private DynamicWebSocketController webSocketController;
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        String path = httpRequest.getRequestURI();
//        System.out.println("In_Filter="+request.toString()+" path="+path);
//        if (path.startsWith("/")) {
//            String socketName = path.substring(1); // Extract socketName from URI
//            try {
//                webSocketController.registerSocketHandler(socketName);
//            } catch (Exception e) {
//                System.err.println("ERROR="+e.toString());
//            }
//        }
//
//        chain.doFilter(request, response);
//    }
}
