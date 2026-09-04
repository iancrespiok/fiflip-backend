package com.fiflip.backend.budget.infrastructure;

import com.fiflip.backend.budget.application.PriceUpdateCommand;
import com.fiflip.backend.budget.application.PricingUseCases;
import com.fiflip.backend.budget.domain.PricingItem;
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

    private final PricingUseCases pricingUseCases;

    public AdminBudgetPricingController(PricingUseCases pricingUseCases) {
        this.pricingUseCases = pricingUseCases;
    }

    public record PriceUpdateRequest(@NotBlank String key, double price) {
        PriceUpdateCommand toCommand() {
            return new PriceUpdateCommand(key, price);
        }
    }

    public record PricingUpdateRequest(@NotEmpty List<PriceUpdateRequest> items) {
    }

    @PutMapping("/pricing")
    public ResponseEntity<List<PricingItem>> updatePricing(@Valid @RequestBody PricingUpdateRequest request) {
        List<PriceUpdateCommand> commands = request.items().stream().map(PriceUpdateRequest::toCommand).toList();
        return ResponseEntity.ok(pricingUseCases.updatePrices(commands));
    }
}
