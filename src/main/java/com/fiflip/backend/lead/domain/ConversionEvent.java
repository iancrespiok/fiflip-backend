package com.fiflip.backend.lead.domain;

public record ConversionEvent(
        String eventName,
        String email,
        String phone,
        String eventId,
        String ipAddress,
        String userAgent
) {
}
