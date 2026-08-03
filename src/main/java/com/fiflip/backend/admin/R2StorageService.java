package com.fiflip.backend.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.util.UUID;

@Service
public class R2StorageService {

    private final String accountId;
    private final String accessKey;
    private final String secretKey;
    private final String bucket;
    private final String publicUrlBase;

    private volatile S3Client s3Client;

    public R2StorageService(
            @Value("${fiflip.r2.account-id}") String accountId,
            @Value("${fiflip.r2.access-key}") String accessKey,
            @Value("${fiflip.r2.secret-key}") String secretKey,
            @Value("${fiflip.r2.bucket}") String bucket,
            @Value("${fiflip.r2.public-url-base}") String publicUrlBase) {
        this.accountId = accountId;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucket = bucket;
        this.publicUrlBase = publicUrlBase;
    }

    public String upload(MultipartFile file) throws java.io.IOException {
        String extension = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }
        String key = "projects/" + UUID.randomUUID() + extension;

        client().putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return publicUrlBase + "/" + key;
    }

    private S3Client client() {
        if (s3Client == null) {
            synchronized (this) {
                if (s3Client == null) {
                    if (accountId.isBlank() || accessKey.isBlank() || secretKey.isBlank()) {
                        throw new IllegalStateException("Cloudflare R2 no está configurado (faltan variables R2_*)");
                    }
                    s3Client = S3Client.builder()
                            .endpointOverride(URI.create("https://" + accountId + ".r2.cloudflarestorage.com"))
                            .region(Region.of("auto"))
                            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                            .build();
                }
            }
        }
        return s3Client;
    }
}
