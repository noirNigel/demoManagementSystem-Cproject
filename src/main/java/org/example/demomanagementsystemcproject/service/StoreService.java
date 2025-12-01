package org.example.demomanagementsystemcproject.service;

import org.example.demomanagementsystemcproject.entity.StoreEntity;
import org.example.demomanagementsystemcproject.repo.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreService {

    private final StoreRepository repo;

    public StoreService(StoreRepository repo) {
        this.repo = repo;
    }

    public List<StoreEntity> list() {
        return repo.findAll();
    }

    public StoreEntity get(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("门店不存在"));
    }

    public StoreEntity save(StoreEntity store) {
        return repo.save(store);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
