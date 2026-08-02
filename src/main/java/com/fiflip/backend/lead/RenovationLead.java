package com.fiflip.backend.lead;

import jakarta.validation.constraints.NotBlank;

public record RenovationLead(
        @NotBlank String nombre,
        @NotBlank String contacto,
        String tipo,
        String ciudad,
        String medidas,
        String descripcion,
        String eventId,
        String ipAddress,
        String userAgent
) {
    public RenovationLead withRequestContext(String ipAddress, String userAgent) {
        return new RenovationLead(nombre, contacto, tipo, ciudad, medidas, descripcion, eventId, ipAddress, userAgent);
    }
}
