package com.ltfullstack.discoveryserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("api/v1/discovery")
public class DiscoveryServerController {
    @GetMapping()
    public String hello(){
        return "Hello Young";
    }
}
