package com.fiflip.backend.budget.application;

import com.fiflip.backend.budget.domain.PricingItem;
import com.fiflip.backend.budget.domain.PricingUnit;
import com.fiflip.backend.budget.domain.RoomType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BudgetCalculationServiceTest {

    private static final List<PricingItem> PRICES = List.of(
            new PricingItem("margin_percent", "Margen", "CONFIG", PricingUnit.PERCENT, 20),
            new PricingItem("waterproofing_material_m2", "Membrana", "TERRAZA", PricingUnit.M2, 1000),
            new PricingItem("waterproofing_labor_m2", "Colocación", "TERRAZA", PricingUnit.M2, 500),
            new PricingItem("water_tank_material_fixed", "Tanque", "TERRAZA", PricingUnit.FIXED, 10000),
            new PricingItem("water_tank_labor_fixed", "Tanque MO", "TERRAZA", PricingUnit.FIXED, 5000),
            new PricingItem("lighting_material_fixed", "Luminaria", "HABITACIÓN", PricingUnit.FIXED, 600),
            new PricingItem("lighting_labor_fixed", "Luminaria MO", "HABITACIÓN", PricingUnit.FIXED, 300));

    private final BudgetCalculationService service = new BudgetCalculationService(new InMemoryCatalog(PRICES));

    @Test
    void terrazaPricesWaterproofingByFloorAreaAndTankAsFixed() {
        RoomInput terraza = new RoomInput(RoomType.TERRAZA, 4, 3, 2.6, Set.of("impermeabilizar", "tanque"));

        BudgetResult result = service.calculate(List.of(terraza));

        // (12 m² * (1000 + 500) + (10000 + 5000)) * 1.20
        assertEquals(List.of(39600.0), result.roomTotals());
        assertEquals(39600.0, result.total());
    }

    @Test
    void pasilloUsesTheSameItemsAndPricesAsHabitacion() {
        Set<String> items = Set.of("luminaria");

        BudgetResult result = service.calculate(List.of(
                new RoomInput(RoomType.HABITACION, 3, 3, 2.6, items),
                new RoomInput(RoomType.PASILLO, 3, 3, 2.6, items)));

        assertEquals(result.roomTotals().get(0), result.roomTotals().get(1));
        assertEquals(1080.0, result.roomTotals().get(1)); // (600 + 300) * 1.20
    }

    @Test
    void returnsOnePricePerRoomInInputOrderAndTotalIsTheirSum() {
        BudgetResult result = service.calculate(List.of(
                new RoomInput(RoomType.HABITACION, 3, 3, 2.6, Set.of("luminaria")),
                new RoomInput(RoomType.TERRAZA, 2, 2, 2.6, Set.of("tanque")),
                new RoomInput(RoomType.HABITACION, 3, 3, 2.6, Set.of())));

        assertEquals(List.of(1080.0, 18000.0, 0.0), result.roomTotals());
        assertEquals(19080.0, result.total());
    }

    @Test
    void roomsAreRoundedToWholePesosSoTheReportAddsUpToTheTotal() {
        // 1 fixed item costing 100.4 with 20% margin = 120.48 -> 120 per room
        PricingCatalogRepository catalog = new InMemoryCatalog(List.of(
                new PricingItem("margin_percent", "Margen", "CONFIG", PricingUnit.PERCENT, 20),
                new PricingItem("lighting_material_fixed", "Luminaria", "HABITACIÓN", PricingUnit.FIXED, 100.4)));
        RoomInput room = new RoomInput(RoomType.HABITACION, 3, 3, 2.6, Set.of("luminaria"));

        BudgetResult result = new BudgetCalculationService(catalog).calculate(List.of(room, room, room));

        assertEquals(List.of(120.0, 120.0, 120.0), result.roomTotals());
        assertEquals(360.0, result.total());
    }

    @Test
    void itemKeyFromAnotherRoomTypeIsIgnored() {
        BudgetResult result = service.calculate(List.of(
                new RoomInput(RoomType.TERRAZA, 4, 3, 2.6, Set.of("luminaria"))));

        assertEquals(List.of(0.0), result.roomTotals());
    }

    private record InMemoryCatalog(List<PricingItem> items) implements PricingCatalogRepository {

        @Override
        public List<PricingItem> findAllOrderedByGroupThenKey() {
            return items;
        }

        @Override
        public Optional<PricingItem> findByKey(String key) {
            return items.stream().filter(i -> i.key().equals(key)).findFirst();
        }

        @Override
        public PricingItem save(PricingItem item) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean existsByKey(String key) {
            return findByKey(key).isPresent();
        }

        @Override
        public void deleteByKey(String key) {
            throw new UnsupportedOperationException();
        }
    }
}
