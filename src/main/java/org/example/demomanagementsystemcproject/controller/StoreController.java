package org.example.demomanagementsystemcproject.controller;

import org.example.demomanagementsystemcproject.entity.StoreEntity;
import org.example.demomanagementsystemcproject.service.StoreService;
import org.example.demomanagementsystemcproject.system.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/stores")
public class StoreController {

    private final StoreService svc;

    public StoreController(StoreService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<StoreEntity> list() {
        return svc.list();
    }

    @GetMapping("/{id}")
    public StoreEntity detail(@PathVariable Long id) {
        return svc.get(id);
    }

    @PostMapping
    @OperationLog(module = "门店管理", action = "新增门店")
    public StoreEntity create(@RequestBody StoreEntity store) {
        if (store.getName() == null || store.getName().isBlank()) {
            throw new RuntimeException("门店名称不能为空");
        }
        return svc.save(store);
    }

    @PutMapping("/{id}")
    @OperationLog(module = "门店管理", action = "更新门店")
    public StoreEntity update(@PathVariable Long id, @RequestBody StoreEntity dto) {
        StoreEntity store = svc.get(id);
        store.setName(dto.getName());
        store.setAddress(dto.getAddress());
        store.setPhone(dto.getPhone());
        store.setOpenTime(dto.getOpenTime());
        store.setCloseTime(dto.getCloseTime());
        store.setStatus(dto.getStatus());
        return svc.save(store);
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "门店管理", action = "删除门店")
    public void delete(@PathVariable Long id) {
        svc.delete(id);
    }
}
