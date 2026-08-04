package com.fiflip.backend.lead;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MetaConversionsService {

    private static final Logger log = LoggerFactory.getLogger(MetaConversionsService.class);

    private final RestClient restClient;
    private final String pixelId;
    private final String accessToken;

    public MetaConversionsService(
            @Value("${fiflip.meta.pixel-id}") String pixelId,
            @Value("${fiflip.meta.capi-token}") String accessToken) {
        this.pixelId = pixelId;
        this.accessToken = accessToken;
        this.restClient = RestClient.builder().baseUrl("https://graph.facebook.com").build();
    }

    public void sendEvent(String eventName, String email, String phone, String eventId, String ipAddress, String userAgent) {
        if (pixelId == null || pixelId.isBlank() || accessToken == null || accessToken.isBlank()) {
            log.debug("Meta Conversions API not configured, skipping event");
            return;
        }
        try {
            Map<String, Object> userData = new HashMap<>();
            if (email != null && !email.isBlank()) {
                userData.put("em", List.of(sha256(email.trim().toLowerCase())));
            }
            if (phone != null && !phone.isBlank()) {
                userData.put("ph", List.of(sha256(phone.replaceAll("[^0-9]", ""))));
            }
            if (ipAddress != null && !ipAddress.isBlank()) {
                userData.put("client_ip_address", ipAddress);
            }
            if (userAgent != null && !userAgent.isBlank()) {
                userData.put("client_user_agent", userAgent);
            }

            Map<String, Object> event = new HashMap<>();
            event.put("event_name", eventName);
            event.put("event_time", Instant.now().getEpochSecond());
            event.put("action_source", "website");
            event.put("user_data", userData);
            if (eventId != null && !eventId.isBlank()) {
                event.put("event_id", eventId);
            }

            Map<String, Object> body = Map.of(
                    "data", List.of(event),
                    "access_token", accessToken);

            restClient.post()
                    .uri("/v21.0/{pixelId}/events", pixelId)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Failed to send Meta Conversions API event", e);
        }
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
