package com.fiflip.backend.budget.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PricingItemJpaRepository extends JpaRepository<PricingItemJpaEntity, String> {
    List<PricingItemJpaEntity> findAllByOrderByGroupAscKeyAsc();
}
