package com.fiflip.backend.budget.infrastructure;

import com.fiflip.backend.budget.domain.PricingItem;

final class PricingItemMapper {

    private PricingItemMapper() {
    }

    static PricingItem toDomain(PricingItemJpaEntity entity) {
        return new PricingItem(entity.getKey(), entity.getLabel(), entity.getGroup(), entity.getUnit(), entity.getPrice());
    }

    static PricingItemJpaEntity toEntity(PricingItem item) {
        return new PricingItemJpaEntity(item.key(), item.label(), item.group(), item.unit(), item.price());
    }
}
