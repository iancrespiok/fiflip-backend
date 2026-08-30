package com.fiflip.backend.budget;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/budget")
public class AdminBudgetPricingController {

    private final PricingItemRepository repository;

    public AdminBudgetPricingController(PricingItemRepository repository) {
        this.repository = repository;
    }

    public record PriceUpdate(@NotBlank String key, double price) {
    }

    public record PricingUpdateRequest(@NotEmpty List<PriceUpdate> items) {
    }

    @PutMapping("/pricing")
    public ResponseEntity<List<PricingItem>> updatePricing(@Valid @RequestBody PricingUpdateRequest request) {
        for (PriceUpdate update : request.items()) {
            repository.findById(update.key()).ifPresent(item -> {
                item.setPrice(update.price());
                repository.save(item);
            });
        }
        return ResponseEntity.ok(repository.findAllByOrderByGroupAscKeyAsc());
    }
}
