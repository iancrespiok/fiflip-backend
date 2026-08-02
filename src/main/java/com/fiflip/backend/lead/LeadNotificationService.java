package com.fiflip.backend.lead;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class LeadNotificationService {

    private static final Logger log = LoggerFactory.getLogger(LeadNotificationService.class);

    private final RestClient restClient;
    private final String fromAddress;
    private final String notifyAddress;

    public LeadNotificationService(
            @Value("${fiflip.resend.api-key}") String resendApiKey,
            @Value("${fiflip.mail.from}") String fromAddress,
            @Value("${fiflip.mail.notify-to}") String notifyAddress) {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .defaultHeader("Authorization", "Bearer " + resendApiKey)
                .build();
        this.fromAddress = fromAddress;
        this.notifyAddress = notifyAddress;
    }

    public void notifyRenovationLead(RenovationLead lead) {
        String body = """
                Nueva solicitud de renovación

                Nombre: %s
                Contacto: %s
                Tipo de proyecto: %s
                Ciudad: %s
                Medidas: %s
                Descripción: %s
                """.formatted(
                lead.nombre(), lead.contacto(), nullToDash(lead.tipo()),
                nullToDash(lead.ciudad()), nullToDash(lead.medidas()), nullToDash(lead.descripcion()));

        send("Fiflip — nueva solicitud de renovación", body);
    }

    public void notifyInvestorLead(InvestorLead lead) {
        String body = """
                Nuevo interesado en invertir

                Nombre: %s
                Contacto: %s
                Monto aproximado: %s
                Mensaje: %s
                """.formatted(
                lead.nombre(), lead.contacto(), nullToDash(lead.monto()), nullToDash(lead.mensaje()));

        send("Fiflip — nuevo interesado en invertir", body);
    }

    private void send(String subject, String body) {
        try {
            restClient.post()
                    .uri("/emails")
                    .body(Map.of(
                            "from", fromAddress,
                            "to", new String[] { notifyAddress },
                            "subject", subject,
                            "text", body))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Failed to send lead notification email", e);
        }
    }

    private static String nullToDash(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }
}
