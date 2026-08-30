package com.fiflip.backend.budget;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PricingItemRepository extends JpaRepository<PricingItem, String> {
    List<PricingItem> findAllByOrderByGroupAscKeyAsc();
}
