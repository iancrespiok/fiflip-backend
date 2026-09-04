package com.fiflip.backend.lead.application;

import com.fiflip.backend.lead.domain.InvestorLead;
import com.fiflip.backend.lead.domain.RenovationLead;

public interface LeadNotifier {
    void notifyRenovationLead(RenovationLead lead);

    void notifyInvestorLead(InvestorLead lead);
}
