package com.fiflip.backend.lead;

import jakarta.validation.constraints.NotBlank;

public record InvestorLead(
        @NotBlank String nombre,
        @NotBlank String email,
        @NotBlank String telefono,
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
