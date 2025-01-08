package com.harshapps.codetogether.handler;

import com.harshapps.codetogether.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/codetogether")
public class TestHandler {

    @Autowired
    private TestService testService;

    @GetMapping("/test")
    public String test(){
        return testService.test();
    }
}
