package com.fiflip.backend.budget.infrastructure;

import com.fiflip.backend.budget.application.PricingUseCases;
import com.fiflip.backend.budget.domain.PricingItem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/budget")
public class BudgetPricingController {

    private final PricingUseCases pricingUseCases;

    public BudgetPricingController(PricingUseCases pricingUseCases) {
        this.pricingUseCases = pricingUseCases;
    }

    @GetMapping("/pricing")
    public List<PricingItem> pricing() {
        return pricingUseCases.listPricing();
    }
}
