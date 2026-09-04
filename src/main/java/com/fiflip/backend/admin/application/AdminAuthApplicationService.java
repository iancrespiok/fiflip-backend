package com.fiflip.backend.admin.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
public class AdminAuthApplicationService implements AdminAuthUseCases {

    private static final long TOKEN_TTL_SECONDS = 24 * 60 * 60;
    private static final String HMAC_ALGO = "HmacSHA256";

    private final String adminPassword;
    private final String tokenSecret;

    public AdminAuthApplicationService(
            @Value("${fiflip.admin.password}") String adminPassword,
            @Value("${fiflip.admin.token-secret}") String tokenSecret) {
        this.adminPassword = adminPassword;
        this.tokenSecret = tokenSecret;
    }

    @Override
    public Optional<String> login(String password) {
        if (adminPassword.isBlank() || password == null || !password.equals(adminPassword)) {
            return Optional.empty();
        }
        return Optional.of(issueToken());
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            String raw = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = raw.split("\\.", 2);
            if (parts.length != 2) {
                return false;
            }
            String payload = parts[0];
            String signature = parts[1];
            String expectedSignature = sign(payload);
            if (!MessageDigest.isEqual(signature.getBytes(StandardCharsets.UTF_8), expectedSignature.getBytes(StandardCharsets.UTF_8))) {
                return false;
            }
            long expiresAt = Long.parseLong(payload);
            return Instant.now().getEpochSecond() < expiresAt;
        } catch (Exception e) {
            return false;
        }
    }

    private String issueToken() {
        long expiresAt = Instant.now().getEpochSecond() + TOKEN_TTL_SECONDS;
        String payload = String.valueOf(expiresAt);
        String signature = sign(payload);
        String raw = payload + "." + signature;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(tokenSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGO));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
