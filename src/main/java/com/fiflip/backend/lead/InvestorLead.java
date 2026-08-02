package com.fiflip.backend.lead;

import jakarta.validation.constraints.NotBlank;

public record InvestorLead(
        @NotBlank String nombre,
        @NotBlank String contacto,
        String monto,
        String mensaje
) {
}
