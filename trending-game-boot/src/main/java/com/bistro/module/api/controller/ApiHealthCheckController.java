package com.bistro.module.api.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiHealthCheckController {


    @GetMapping(value = "/health-check")
    public Object healthCheck() {
        return "ok";
    }

}
