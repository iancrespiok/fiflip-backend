package com.fiflip.backend.budget;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/budget")
public class BudgetPricingController {

    private final PricingItemRepository repository;

    public BudgetPricingController(PricingItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/pricing")
    public List<PricingItem> pricing() {
        return repository.findAllByOrderByGroupAscKeyAsc();
    }
}
