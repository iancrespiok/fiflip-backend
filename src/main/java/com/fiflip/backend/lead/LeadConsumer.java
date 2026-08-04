package com.fiflip.backend.lead;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LeadConsumer {

    private static final Logger log = LoggerFactory.getLogger(LeadConsumer.class);

    private final LeadNotificationService notificationService;
    private final MetaConversionsService metaConversionsService;

    public LeadConsumer(LeadNotificationService notificationService, MetaConversionsService metaConversionsService) {
        this.notificationService = notificationService;
        this.metaConversionsService = metaConversionsService;
    }

    @KafkaListener(topics = LeadTopics.RENOVATION, groupId = "fiflip-backend")
    public void onRenovationLead(RenovationLead lead) {
        log.info("Renovation lead received from {}", lead.email());
        notificationService.notifyRenovationLead(lead);
        metaConversionsService.sendEvent(
                "Lead", lead.email(), lead.telefono(), lead.eventId(), lead.ipAddress(), lead.userAgent());
        metaConversionsService.sendEvent(
                renovationCustomEventName(lead.tipo()), lead.email(), lead.telefono(), lead.customEventId(), lead.ipAddress(), lead.userAgent());
    }

    @KafkaListener(topics = LeadTopics.INVESTOR, groupId = "fiflip-backend")
    public void onInvestorLead(InvestorLead lead) {
        log.info("Investor lead received from {}", lead.email());
        notificationService.notifyInvestorLead(lead);
        metaConversionsService.sendEvent(
                "Lead", lead.email(), lead.telefono(), lead.eventId(), lead.ipAddress(), lead.userAgent());
        metaConversionsService.sendEvent(
                "LeadInversion", lead.email(), lead.telefono(), lead.customEventId(), lead.ipAddress(), lead.userAgent());
    }

    private static String renovationCustomEventName(String tipo) {
        if (tipo == null) {
            return "LeadRenovacionOtro";
        }
        return switch (tipo) {
            case "Vender más caro" -> "LeadVenderMasCaro";
            case "Lista para mudarte" -> "LeadListoParaMudarte";
            case "Refaccionar mi espacio" -> "LeadRefaccion";
            default -> "LeadRenovacionOtro";
        };
    }
}
