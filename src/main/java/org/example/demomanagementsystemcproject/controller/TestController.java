package org.example.demomanagementsystemcproject.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public Map<String, Object> publicEndpoint() {
        return Map.of(
                "message", "This is a public endpoint",
                "timestamp", System.currentTimeMillis()
        );
    }

    @GetMapping("/protected")
    public Map<String, Object> protectedEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Map.of(
                "message", "This is a protected endpoint",
                "user", auth.getName(),
                "authenticated", auth.isAuthenticated(),
                "authorities", auth.getAuthorities(),
                "timestamp", System.currentTimeMillis()
        );
    }
}