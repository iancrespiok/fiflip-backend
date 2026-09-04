package com.fiflip.backend.lead.infrastructure;

import com.fiflip.backend.lead.domain.InvestorLead;
import jakarta.validation.constraints.NotBlank;

public record InvestorLeadRequest(
        @NotBlank String nombre,
        @NotBlank String email,
        @NotBlank String telefono,
        String monto,
        String mensaje,
        String eventId,
        String customEventId
) {
    public InvestorLead toDomain() {
        return new InvestorLead(nombre, email, telefono, monto, mensaje, eventId, customEventId, null, null);
    }
}
