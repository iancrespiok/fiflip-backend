package com.fiflip.backend.lead.application;

import com.fiflip.backend.lead.domain.InvestorLead;
import com.fiflip.backend.lead.domain.RenovationLead;
import com.fiflip.backend.storage.UploadedFile;

public interface LeadUseCases {
    void submitRenovationLead(RenovationLead lead);

    void submitInvestorLead(InvestorLead lead);

    String uploadLeadImage(UploadedFile file);
}
