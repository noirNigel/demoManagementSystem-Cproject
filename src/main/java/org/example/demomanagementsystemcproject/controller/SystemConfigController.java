package org.example.demomanagementsystemcproject.controller;

import org.example.demomanagementsystemcproject.entity.SystemConfigEntity;
import org.example.demomanagementsystemcproject.service.SystemConfigService;
import org.example.demomanagementsystemcproject.system.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/config")
public class SystemConfigController {

    private final SystemConfigService svc;

    public SystemConfigController(SystemConfigService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<SystemConfigEntity> list() {
        return svc.list();
    }

    @GetMapping("/{key}")
    public SystemConfigEntity get(@PathVariable String key) {
        return svc.getByKey(key);
    }

    @PostMapping
    @OperationLog(module = "系统设置", action = "保存配置")
    public SystemConfigEntity save(@RequestBody SystemConfigEntity config) {
        if (config.getConfigKey() == null || config.getConfigKey().isBlank()) {
            throw new RuntimeException("配置 key 不能为空");
        }
        return svc.save(config);
    }
}
