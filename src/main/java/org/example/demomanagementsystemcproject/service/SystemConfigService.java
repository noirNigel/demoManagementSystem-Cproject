package org.example.demomanagementsystemcproject.service;

import org.example.demomanagementsystemcproject.entity.SystemConfigEntity;
import org.example.demomanagementsystemcproject.repo.SystemConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemConfigService {

    private final SystemConfigRepository repo;

    public SystemConfigService(SystemConfigRepository repo) {
        this.repo = repo;
    }

    public List<SystemConfigEntity> list() {
        return repo.findAll();
    }

    public SystemConfigEntity getByKey(String key) {
        return repo.findByConfigKey(key);
    }

    public SystemConfigEntity save(SystemConfigEntity cfg) {
        SystemConfigEntity exist = repo.findByConfigKey(cfg.getConfigKey());
        if (exist != null) {
            exist.setConfigValue(cfg.getConfigValue());
            exist.setDescription(cfg.getDescription());
            return repo.save(exist);
        }
        return repo.save(cfg);
    }
}
