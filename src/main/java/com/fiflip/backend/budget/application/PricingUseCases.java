package com.fiflip.backend.budget.application;

import com.fiflip.backend.budget.domain.PricingItem;

import java.util.List;

public interface PricingUseCases {

    List<PricingItem> listPricing();

    List<PricingItem> updatePrices(List<PriceUpdateCommand> updates);
}
