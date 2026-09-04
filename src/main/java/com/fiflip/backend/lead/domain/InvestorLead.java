package com.fiflip.backend.lead.domain;

public record InvestorLead(
        String nombre,
        String email,
        String telefono,
        String monto,
        String mensaje,
        String eventId,
        String customEventId,
        String ipAddress,
        String userAgent
) {
    public InvestorLead withRequestContext(String ipAddress, String userAgent) {
        return new InvestorLead(nombre, email, telefono, monto, mensaje, eventId, customEventId, ipAddress, userAgent);
    }
}
