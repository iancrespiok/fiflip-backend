package com.fiflip.backend.budget.application;

import com.fiflip.backend.budget.domain.ItemPricing;
import com.fiflip.backend.budget.domain.PricingItem;
import com.fiflip.backend.budget.domain.RoomItemCatalog;
import com.fiflip.backend.budget.domain.Surface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BudgetCalculationService implements BudgetCalculationUseCases {

    private static final double PAINT_BUCKET_COVERAGE_M2 = 100;
    private static final double PUTTY_BUCKET_COVERAGE_M2 = 60;
    private static final double PRIMER_COVERAGE_M2 = 60;

    private final PricingCatalogRepository repository;

    public BudgetCalculationService(PricingCatalogRepository repository) {
        this.repository = repository;
    }

    @Override
    public double calculateTotal(List<RoomInput> rooms) {
        Map<String, Double> prices = repository.findAllOrderedByGroupThenKey().stream()
                .collect(Collectors.toMap(PricingItem::key, PricingItem::price));

        double subtotal = rooms.stream().mapToDouble(room -> roomSubtotal(room, prices)).sum();
        double marginPercent = price(prices, "margin_percent");
        return subtotal * (1 + marginPercent / 100);
    }

    private double roomSubtotal(RoomInput room, Map<String, Double> prices) {
        Map<String, ItemPricing> catalog = RoomItemCatalog.itemsFor(room.type());
        double sum = 0;
        for (String key : room.itemKeys()) {
            ItemPricing pricing = catalog.get(key);
            if (pricing == null) {
                continue; // unknown item key for this room type: silently ignored
            }
            sum += itemCost(pricing, room, prices);
        }
        return sum;
    }

    private double itemCost(ItemPricing pricing, RoomInput room, Map<String, Double> prices) {
        return switch (pricing) {
            case ItemPricing.FixedSplit(String materialKey, String laborKey) ->
                    price(prices, materialKey) + price(prices, laborKey);
            case ItemPricing.AreaSplit(String materialKey, String laborKey, Surface surface) -> {
                double area = surface == Surface.WALL ? wallAreaM2(room) : roomFloorM2(room);
                yield (price(prices, materialKey) + price(prices, laborKey)) * area;
            }
            case ItemPricing.Paint ignored -> paintCost(wallAreaM2(room), prices);
        };
    }

    private double paintCost(double wallM2, Map<String, Double> prices) {
        double labor = price(prices, "paint_labor_m2") * wallM2;
        double paint = unitsNeeded(wallM2, PAINT_BUCKET_COVERAGE_M2) * price(prices, "paint_bucket_price");
        double putty = unitsNeeded(wallM2, PUTTY_BUCKET_COVERAGE_M2) * price(prices, "putty_bucket_price");
        double primer = unitsNeeded(wallM2, PRIMER_COVERAGE_M2) * price(prices, "primer_price");
        return labor + paint + putty + primer;
    }

    private static double roomFloorM2(RoomInput room) {
        return room.largo() * room.ancho();
    }

    private static double wallAreaM2(RoomInput room) {
        return 2 * (room.largo() + room.ancho()) * room.altura();
    }

    private static long unitsNeeded(double m2, double coverage) {
        return coverage > 0 ? (long) Math.ceil(m2 / coverage) : 0;
    }

    private static double price(Map<String, Double> prices, String key) {
        return prices.getOrDefault(key, 0.0);
    }
}
