package org.example.demomanagementsystemcproject.service;

import org.example.demomanagementsystemcproject.entity.OperationLogEntity;
import org.example.demomanagementsystemcproject.repo.OperationLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogService {

    private final OperationLogRepository repo;

    public OperationLogService(OperationLogRepository repo) {
        this.repo = repo;
    }

    public void save(OperationLogEntity log) {
        repo.save(log);
    }

    public List<OperationLogEntity> list() {
        return repo.findAllOrderByCreatedAtDesc();
    }
}
