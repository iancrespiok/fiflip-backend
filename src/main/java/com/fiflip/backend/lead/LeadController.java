package com.fiflip.backend.lead;

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
    public ResponseEntity<Map<String, String>> renovation(@Valid @RequestBody RenovationLead lead) {
        kafkaTemplate.send(LeadTopics.RENOVATION, lead.contacto(), lead);
        return ResponseEntity.accepted().body(Map.of("status", "received"));
    }

    @PostMapping("/investor")
    public ResponseEntity<Map<String, String>> investor(@Valid @RequestBody InvestorLead lead) {
        kafkaTemplate.send(LeadTopics.INVESTOR, lead.contacto(), lead);
        return ResponseEntity.accepted().body(Map.of("status", "received"));
    }
}
