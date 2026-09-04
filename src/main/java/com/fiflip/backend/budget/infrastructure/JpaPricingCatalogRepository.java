package com.fiflip.backend.budget.infrastructure;

import com.fiflip.backend.budget.application.PricingCatalogRepository;
import com.fiflip.backend.budget.domain.PricingItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaPricingCatalogRepository implements PricingCatalogRepository {

    private final PricingItemJpaRepository jpaRepository;

    public JpaPricingCatalogRepository(PricingItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<PricingItem> findAllOrderedByGroupThenKey() {
        return jpaRepository.findAllByOrderByGroupAscKeyAsc().stream()
                .map(PricingItemMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<PricingItem> findByKey(String key) {
        return jpaRepository.findById(key).map(PricingItemMapper::toDomain);
    }

    @Override
    public PricingItem save(PricingItem item) {
        PricingItemJpaEntity saved = jpaRepository.save(PricingItemMapper.toEntity(item));
        return PricingItemMapper.toDomain(saved);
    }

    @Override
    public boolean existsByKey(String key) {
        return jpaRepository.existsById(key);
    }

    @Override
    public void deleteByKey(String key) {
        jpaRepository.findById(key).ifPresent(jpaRepository::delete);
    }
}
