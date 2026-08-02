package com.fiflip.backend.lead;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class LeadNotificationService {

    private static final Logger log = LoggerFactory.getLogger(LeadNotificationService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String notifyAddress;

    public LeadNotificationService(
            JavaMailSender mailSender,
            @Value("${fiflip.mail.from}") String fromAddress,
            @Value("${fiflip.mail.notify-to}") String notifyAddress) {
        this.mailSender = mailSender;
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
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(notifyAddress);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send lead notification email", e);
        }
    }

    private static String nullToDash(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }
}
