package com.fiflip.backend.budget.infrastructure;

import com.fiflip.backend.budget.application.BudgetCalculationUseCases;
import com.fiflip.backend.budget.application.PricingUseCases;
import com.fiflip.backend.budget.application.RoomInput;
import com.fiflip.backend.budget.domain.PricingItem;
import com.fiflip.backend.budget.domain.RoomType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/budget")
public class BudgetPricingController {

    private final PricingUseCases pricingUseCases;
    private final BudgetCalculationUseCases budgetCalculationUseCases;

    public BudgetPricingController(PricingUseCases pricingUseCases, BudgetCalculationUseCases budgetCalculationUseCases) {
        this.pricingUseCases = pricingUseCases;
        this.budgetCalculationUseCases = budgetCalculationUseCases;
    }

    @GetMapping("/pricing")
    public List<PricingItem> pricing() {
        return pricingUseCases.listPricing();
    }

    public record RoomRequest(
            @NotNull RoomType type,
            @Positive double largo,
            @Positive double ancho,
            @Positive double altura,
            @NotNull Set<String> itemKeys) {

        RoomInput toInput() {
            return new RoomInput(type, largo, ancho, altura, itemKeys);
        }
    }

    public record CalculateRequest(@NotEmpty @Valid List<RoomRequest> rooms) {
    }

    public record CalculateResponse(double total) {
    }

    @PostMapping("/calculate")
    public CalculateResponse calculate(@Valid @RequestBody CalculateRequest request) {
        List<RoomInput> rooms = request.rooms().stream().map(RoomRequest::toInput).toList();
        return new CalculateResponse(budgetCalculationUseCases.calculateTotal(rooms));
    }
}
