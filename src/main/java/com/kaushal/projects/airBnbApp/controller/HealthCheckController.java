package com.kaushal.projects.airBnbApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    //This route will be checked by elastic beanstalk in order to determine that our application is working
    @GetMapping("/")
    public ResponseEntity<String> healthCheck()
    {
        return ResponseEntity.ok("ok");
    }
}
