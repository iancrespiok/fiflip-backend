package com.fiflip.backend.budget.domain;

public sealed interface ItemPricing permits ItemPricing.FixedSplit, ItemPricing.AreaSplit, ItemPricing.Paint {

    record FixedSplit(String materialKey, String laborKey) implements ItemPricing {
    }

    record AreaSplit(String materialKey, String laborKey, Surface surface) implements ItemPricing {
    }

    /** Paint priced on the walls, plus the ceiling (same area as the floor) when {@code includesCeiling}. */
    record Paint(boolean includesCeiling) implements ItemPricing {
    }
}
