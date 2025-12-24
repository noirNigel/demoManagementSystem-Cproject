package org.example.demomanagementsystemcproject.service;

import org.example.demomanagementsystemcproject.entity.StoreEntity;
import org.example.demomanagementsystemcproject.repo.StoreRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        store.setOpenTime(normalizeTime(store.getOpenTime()));
        store.setCloseTime(normalizeTime(store.getCloseTime()));
        return repo.save(store);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    private String normalizeTime(String time) {
        if (time == null) {
            return null;
        }
        String cleaned = time.trim().replace('：', ':');
        if (cleaned.isEmpty()) {
            return null;
        }

        DateTimeFormatter parser = DateTimeFormatter.ofPattern("H:mm[:ss]");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        try {
            LocalTime parsed = LocalTime.parse(cleaned, parser);
            return parsed.format(formatter);
        } catch (DateTimeParseException ex) {
            throw new RuntimeException("营业时间格式不正确，请使用 08:30 或 08:30:00 这样的格式", ex);
        }
    }
}
