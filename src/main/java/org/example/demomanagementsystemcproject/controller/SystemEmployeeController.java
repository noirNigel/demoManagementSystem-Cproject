package org.example.demomanagementsystemcproject.controller;

import org.example.demomanagementsystemcproject.entity.Admin;
import org.example.demomanagementsystemcproject.repository.AdminRepository;
import org.example.demomanagementsystemcproject.system.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/admins")
public class SystemEmployeeController {

    private final AdminRepository adminRepository;

    public SystemEmployeeController(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    // 员工列表
    @GetMapping
    public List<Admin> list() {
        return adminRepository.findAll();
    }

    // 员工详情
    @GetMapping("/{id}")
    public Admin detail(@PathVariable Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("员工不存在"));
    }

    // 新增员工
    @PostMapping
    @OperationLog(module = "员工管理", action = "新增员工")
    public Admin create(@RequestBody Admin admin) {
        if (admin.getUsername() == null || admin.getUsername().isBlank()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (adminRepository.findByUsername(admin.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (admin.getPassword() == null || admin.getPassword().length() < 6) {
            throw new RuntimeException("密码至少 6 位");
        }
        if (admin.getRole() == null || admin.getRole().isBlank()) {
            admin.setRole("USER");
        }
        admin.setStatus(1);
        return adminRepository.save(admin);
    }

    // 修改员工
    @PutMapping("/{id}")
    @OperationLog(module = "员工管理", action = "修改员工")
    public Admin update(@PathVariable Long id, @RequestBody Admin dto) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("员工不存在"));

        if (dto.getRole() != null) {
            admin.setRole(dto.getRole());
        }
        if (dto.getStatus() != null) {
            admin.setStatus(dto.getStatus());
        }
        admin.setStoreId(dto.getStoreId());

        return adminRepository.save(admin);
    }

    // 禁用员工
    @DeleteMapping("/{id}")
    @OperationLog(module = "员工管理", action = "禁用员工")
    public void disable(@PathVariable Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("员工不存在"));
        admin.setStatus(0);
        adminRepository.save(admin);
    }
}
