package com.fiflip.backend.lead.application;

import com.fiflip.backend.lead.domain.ConversionEvent;
import com.fiflip.backend.lead.domain.InvestorLead;
import com.fiflip.backend.lead.domain.RenovationLead;
import com.fiflip.backend.storage.ObjectStorage;
import com.fiflip.backend.storage.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LeadApplicationService implements LeadUseCases {

    private static final Logger log = LoggerFactory.getLogger(LeadApplicationService.class);

    private final LeadNotifier notifier;
    private final ConversionEventPublisher conversionEventPublisher;
    private final ObjectStorage objectStorage;

    public LeadApplicationService(LeadNotifier notifier, ConversionEventPublisher conversionEventPublisher, ObjectStorage objectStorage) {
        this.notifier = notifier;
        this.conversionEventPublisher = conversionEventPublisher;
        this.objectStorage = objectStorage;
    }

    @Override
    public void submitRenovationLead(RenovationLead lead) {
        log.info("Renovation lead received from {}", lead.email());
        notifier.notifyRenovationLead(lead);
        conversionEventPublisher.publish(new ConversionEvent(
                "Lead", lead.email(), lead.telefono(), lead.eventId(), lead.ipAddress(), lead.userAgent()));
        conversionEventPublisher.publish(new ConversionEvent(
                lead.customEventName(), lead.email(), lead.telefono(), lead.customEventId(), lead.ipAddress(), lead.userAgent()));
    }

    @Override
    public void submitInvestorLead(InvestorLead lead) {
        log.info("Investor lead received from {}", lead.email());
        notifier.notifyInvestorLead(lead);
        conversionEventPublisher.publish(new ConversionEvent(
                "Lead", lead.email(), lead.telefono(), lead.eventId(), lead.ipAddress(), lead.userAgent()));
        conversionEventPublisher.publish(new ConversionEvent(
                "LeadInversion", lead.email(), lead.telefono(), lead.customEventId(), lead.ipAddress(), lead.userAgent()));
    }

    @Override
    public String uploadLeadImage(UploadedFile file) {
        return objectStorage.upload(file, "leads");
    }
}
