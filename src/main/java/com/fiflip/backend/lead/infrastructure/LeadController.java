package com.fiflip.backend.lead.infrastructure;

import com.fiflip.backend.lead.application.LeadUseCases;
import com.fiflip.backend.lead.domain.InvestorLead;
import com.fiflip.backend.lead.domain.RenovationLead;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadUseCases leadUseCases;

    public LeadController(LeadUseCases leadUseCases) {
        this.leadUseCases = leadUseCases;
    }

    @PostMapping("/renovation")
    public ResponseEntity<Map<String, String>> renovation(@Valid @RequestBody RenovationLeadRequest body, HttpServletRequest httpRequest) {
        RenovationLead enriched = body.toDomain().withRequestContext(clientIp(httpRequest), httpRequest.getHeader("User-Agent"));
        leadUseCases.submitRenovationLead(enriched);
        return ResponseEntity.ok(Map.of("status", "received"));
    }

    @PostMapping("/investor")
    public ResponseEntity<Map<String, String>> investor(@Valid @RequestBody InvestorLeadRequest body, HttpServletRequest httpRequest) {
        InvestorLead enriched = body.toDomain().withRequestContext(clientIp(httpRequest), httpRequest.getHeader("User-Agent"));
        leadUseCases.submitInvestorLead(enriched);
        return ResponseEntity.ok(Map.of("status", "received"));
    }

    private static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
