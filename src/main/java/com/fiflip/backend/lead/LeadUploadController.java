package com.fiflip.backend.lead;

import com.fiflip.backend.admin.R2StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/leads")
public class LeadUploadController {

    private final R2StorageService storageService;

    public LeadUploadController(R2StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/uploads")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(Map.of("error", "only images allowed"));
        }
        String url = storageService.upload(file, "leads");
        return ResponseEntity.ok(Map.of("url", url));
    }
}
