package com.fiflip.backend.budget.domain;

public sealed interface ItemPricing permits ItemPricing.FixedSplit, ItemPricing.AreaSplit, ItemPricing.Paint {

    record FixedSplit(String materialKey, String laborKey) implements ItemPricing {
    }

    record AreaSplit(String materialKey, String laborKey, Surface surface) implements ItemPricing {
    }

    record Paint() implements ItemPricing {
    }
}
