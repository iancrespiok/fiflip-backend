package com.fiflip.backend.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminUploadController {

    private final R2StorageService storageService;

    public AdminUploadController(R2StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/uploads")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String url = storageService.upload(file);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
