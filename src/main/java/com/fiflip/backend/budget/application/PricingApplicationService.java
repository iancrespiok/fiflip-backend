package com.fiflip.backend.budget.application;

import com.fiflip.backend.budget.domain.PricingItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PricingApplicationService implements PricingUseCases {

    private final PricingCatalogRepository repository;

    public PricingApplicationService(PricingCatalogRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PricingItem> listPricing() {
        return repository.findAllOrderedByGroupThenKey();
    }

    @Override
    public List<PricingItem> updatePrices(List<PriceUpdateCommand> updates) {
        for (PriceUpdateCommand update : updates) {
            repository.findByKey(update.key())
                    .map(item -> item.withPrice(update.price()))
                    .ifPresent(repository::save);
        }
        return repository.findAllOrderedByGroupThenKey();
    }
}
