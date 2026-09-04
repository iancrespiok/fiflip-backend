package com.fiflip.backend.lead.application;

import com.fiflip.backend.lead.domain.ConversionEvent;

public interface ConversionEventPublisher {
    void publish(ConversionEvent event);
}
