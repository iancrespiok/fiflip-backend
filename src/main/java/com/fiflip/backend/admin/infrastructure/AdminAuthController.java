package com.fiflip.backend.admin.infrastructure;

import com.fiflip.backend.admin.application.AdminAuthUseCases;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final AdminAuthUseCases adminAuthUseCases;

    public AdminAuthController(AdminAuthUseCases adminAuthUseCases) {
        this.adminAuthUseCases = adminAuthUseCases;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        return adminAuthUseCases.login(request.password())
                .map(token -> ResponseEntity.ok(Map.of("token", token)))
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error", "Contraseña incorrecta")));
    }
}
