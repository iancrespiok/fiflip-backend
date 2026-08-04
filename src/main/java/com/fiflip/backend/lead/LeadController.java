package com.fiflip.backend.lead;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public LeadController(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/renovation")
    public ResponseEntity<Map<String, String>> renovation(@Valid @RequestBody RenovationLead lead, HttpServletRequest request) {
        RenovationLead enriched = lead.withRequestContext(clientIp(request), request.getHeader("User-Agent"));
        kafkaTemplate.send(LeadTopics.RENOVATION, enriched.email(), enriched);
        return ResponseEntity.accepted().body(Map.of("status", "received"));
    }

    @PostMapping("/investor")
    public ResponseEntity<Map<String, String>> investor(@Valid @RequestBody InvestorLead lead, HttpServletRequest request) {
        InvestorLead enriched = lead.withRequestContext(clientIp(request), request.getHeader("User-Agent"));
        kafkaTemplate.send(LeadTopics.INVESTOR, enriched.email(), enriched);
        return ResponseEntity.accepted().body(Map.of("status", "received"));
    }

    private static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
