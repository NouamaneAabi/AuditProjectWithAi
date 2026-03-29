package com.audit.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "Bienvenue sur l'API Audit Backend";
    }

    @GetMapping("/test")
    public String test() {
        return "L'application fonctionne ! 🚀";
    }
}