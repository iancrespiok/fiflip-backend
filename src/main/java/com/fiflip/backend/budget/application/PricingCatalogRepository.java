package com.fiflip.backend.budget.application;

import com.fiflip.backend.budget.domain.PricingItem;

import java.util.List;
import java.util.Optional;

public interface PricingCatalogRepository {

    List<PricingItem> findAllOrderedByGroupThenKey();

    Optional<PricingItem> findByKey(String key);

    PricingItem save(PricingItem item);

    boolean existsByKey(String key);

    // Must be a no-op when the key is already absent (find-then-delete), never a raw
    // deleteById — PricingSeeder calls this on every boot for already-removed keys.
    void deleteByKey(String key);
}
