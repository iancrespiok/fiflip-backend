package com.fiflip.backend.lead;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LeadConsumer {

    private static final Logger log = LoggerFactory.getLogger(LeadConsumer.class);

    private final LeadNotificationService notificationService;

    public LeadConsumer(LeadNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = LeadTopics.RENOVATION, groupId = "fiflip-backend")
    public void onRenovationLead(RenovationLead lead) {
        log.info("Renovation lead received from {}", lead.contacto());
        notificationService.notifyRenovationLead(lead);
    }

    @KafkaListener(topics = LeadTopics.INVESTOR, groupId = "fiflip-backend")
    public void onInvestorLead(InvestorLead lead) {
        log.info("Investor lead received from {}", lead.contacto());
        notificationService.notifyInvestorLead(lead);
    }
}
