package com.fiflip.backend.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final AdminTokenService tokenService;
    private final String adminPassword;

    public AdminAuthController(AdminTokenService tokenService, @Value("${fiflip.admin.password}") String adminPassword) {
        this.tokenService = tokenService;
        this.adminPassword = adminPassword;
    }

    public record LoginRequest(String password) {
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        if (adminPassword.isBlank() || request.password() == null || !request.password().equals(adminPassword)) {
            return ResponseEntity.status(401).body(Map.of("error", "Contraseña incorrecta"));
        }
        return ResponseEntity.ok(Map.of("token", tokenService.issueToken()));
    }
}
