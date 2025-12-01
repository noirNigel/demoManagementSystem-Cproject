package org.example.demomanagementsystemcproject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping
    public Map<String, Object> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "后台管理系统 API");
        response.put("status", "运行中");
        response.put("timestamp", System.currentTimeMillis());
        response.put("endpoints", Map.of(
                "登录", "POST /api/auth/login",
                "公共测试", "GET /api/test/public",
                "保护测试", "GET /api/test/protected"
        ));
        return response;
    }
}