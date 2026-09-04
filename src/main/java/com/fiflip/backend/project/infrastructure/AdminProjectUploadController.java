package com.fiflip.backend.project.infrastructure;

import com.fiflip.backend.project.application.ProjectUseCases;
import com.fiflip.backend.storage.UploadedFile;
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
public class AdminProjectUploadController {

    private final ProjectUseCases projectUseCases;

    public AdminProjectUploadController(ProjectUseCases projectUseCases) {
        this.projectUseCases = projectUseCases;
    }

    @PostMapping("/uploads")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        UploadedFile uploadedFile = new UploadedFile(file.getBytes(), file.getOriginalFilename(), file.getContentType());
        String url = projectUseCases.uploadProjectImage(uploadedFile);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
