package com.fiflip.backend.lead;

import jakarta.validation.constraints.NotBlank;

public record RenovationLead(
        @NotBlank String nombre,
        @NotBlank String email,
        @NotBlank String telefono,
        String tipo,
        String ciudad,
        String medidas,
        String descripcion,
        String eventId,
        String customEventId,
        String ipAddress,
        String userAgent
) {
    public RenovationLead withRequestContext(String ipAddress, String userAgent) {
        return new RenovationLead(nombre, email, telefono, tipo, ciudad, medidas, descripcion, eventId, customEventId, ipAddress, userAgent);
    }
}
