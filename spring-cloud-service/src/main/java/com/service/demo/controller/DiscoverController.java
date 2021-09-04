package com.service.demo.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.service.demo.config.ConfigServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class DiscoverController {

    @Autowired
    @LoadBalanced
    public RestTemplate loadBalance;

    @Autowired
    public ConfigServer configServer;

    @Value("${spring.application.name}")
    public String appName;

    @GetMapping("/discover/local")
    @ResponseBody
    public ResponseEntity discoverLocal() {
        String s = loadBalance.getForObject("http://servicelocal/name", String.class);
        return new ResponseEntity(s, HttpStatus.OK);
    }

    @GetMapping("/discover/test")
    @ResponseBody
    @HystrixCommand(fallbackMethod = "fallback")
    public ResponseEntity discoverTest() {
        String s = loadBalance.getForObject("http://servicetest/name", String.class);
        return new ResponseEntity(s, HttpStatus.OK);
    }

    public ResponseEntity fallback() {
        return new ResponseEntity("fallback", HttpStatus.OK);
    }

    @GetMapping("/name")
    @ResponseBody
    public ResponseEntity name() {
        return new ResponseEntity(appName, HttpStatus.OK);
    }

    @GetMapping("/key")
    public ResponseEntity key() {
        return new ResponseEntity(configServer.key, HttpStatus.OK);
    }
}
