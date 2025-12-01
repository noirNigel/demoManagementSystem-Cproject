package org.example.demomanagementsystemcproject.service.impl;

import org.example.demomanagementsystemcproject.entity.Admin;
import org.example.demomanagementsystemcproject.repository.AdminRepository;
import org.example.demomanagementsystemcproject.service.AdminService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ 只能注入这两个，不能再有 SecurityConfig / JwtFilter / AuthenticationManager
    public AdminServiceImpl(AdminRepository adminRepository,
                            PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Admin login(String username, String password) {
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null) {
            return null;
        }
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            return null;
        }
        return admin;
    }

    @Override
    public Admin register(String username, String password, String confirmPassword) {
        if (adminRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        if (!password.equals(confirmPassword)) {
            throw new RuntimeException("两次密码不一致");
        }
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole("ADMIN");
        return adminRepository.save(admin);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return adminRepository.existsByUsername(username);
    }

    @Override
    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }
}
