package com.fiflip.backend.budget.domain;

public record PricingItem(String key, String label, String group, PricingUnit unit, double price) {

    public PricingItem withPrice(double newPrice) {
        return new PricingItem(key, label, group, unit, newPrice);
    }
}
