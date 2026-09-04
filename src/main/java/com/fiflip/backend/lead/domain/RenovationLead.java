package com.fiflip.backend.lead.domain;

import java.util.List;

public record RenovationLead(
        String nombre,
        String email,
        String telefono,
        String tipo,
        String ciudad,
        String medidas,
        String descripcion,
        List<String> fotoUrls,
        String eventId,
        String customEventId,
        String ipAddress,
        String userAgent
) {
    public RenovationLead withRequestContext(String ipAddress, String userAgent) {
        return new RenovationLead(
                nombre, email, telefono, tipo, ciudad, medidas, descripcion, fotoUrls, eventId, customEventId, ipAddress, userAgent);
    }

    public String customEventName() {
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
