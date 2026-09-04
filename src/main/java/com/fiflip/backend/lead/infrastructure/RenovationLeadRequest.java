package com.fiflip.backend.lead.infrastructure;

import com.fiflip.backend.lead.domain.RenovationLead;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record RenovationLeadRequest(
        @NotBlank String nombre,
        @NotBlank String email,
        @NotBlank String telefono,
        String tipo,
        String ciudad,
        String medidas,
        String descripcion,
        List<String> fotoUrls,
        String eventId,
        String customEventId
) {
    public RenovationLead toDomain() {
        return new RenovationLead(nombre, email, telefono, tipo, ciudad, medidas, descripcion, fotoUrls, eventId, customEventId, null, null);
    }
}
