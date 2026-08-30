package com.fiflip.backend.budget;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Inserts default pricing rows on first boot so the calculator isn't
 * showing $0 before an admin has a chance to configure real prices.
 * These defaults are placeholders — they must be reviewed in /admin.
 */
@Component
public class PricingSeeder implements CommandLineRunner {

    private final PricingItemRepository repository;

    public PricingSeeder(PricingItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(List.of(
                new PricingItem("floor_m2", "Piso (mano de obra + material) — por m²", "GENERAL", PricingUnit.M2, 45000),
                new PricingItem("paint_m2", "Pintura (mano de obra + material) — por m²", "GENERAL", PricingUnit.M2, 12000),
                new PricingItem("ceiling_gypsum_m2", "Cielorraso de placas de yeso con dicroicos — por m²", "GENERAL", PricingUnit.M2, 38000),
                new PricingItem("wall_covering_m2", "Revestimientos — por m²", "GENERAL", PricingUnit.M2, 42000),
                new PricingItem("door_window_fixed", "Cambio de abertura (puerta/ventana)", "GENERAL", PricingUnit.FIXED, 180000),
                new PricingItem("outlets_fixed", "Cambio de enchufes", "GENERAL", PricingUnit.FIXED, 35000),
                new PricingItem("ac_fixed", "Instalación de aire acondicionado", "GENERAL", PricingUnit.FIXED, 650000),

                new PricingItem("sanitary_fixed", "Cambio de sanitarios (inodoro/bidet)", "BAÑO", PricingUnit.FIXED, 220000),
                new PricingItem("shower_glass_fixed", "Cambio de bañadera por ducha con mampara de vidrio", "BAÑO", PricingUnit.FIXED, 480000),
                new PricingItem("vanity_mirror_fixed", "Vanitory con espejo nuevo", "BAÑO", PricingUnit.FIXED, 260000),

                new PricingItem("kitchen_expand_fixed", "Ampliación del espacio", "COCINA", PricingUnit.FIXED, 900000),
                new PricingItem("kitchen_furniture_fixed", "Cambio de muebles", "COCINA", PricingUnit.FIXED, 850000),
                new PricingItem("faucet_fixed", "Cambio de griferías", "COCINA", PricingUnit.FIXED, 120000),
                new PricingItem("countertop_fixed", "Cambio de mesadas", "COCINA", PricingUnit.FIXED, 380000),

                new PricingItem("closet_doors_fixed", "Cambio de puertas de placar", "HABITACIÓN", PricingUnit.FIXED, 150000),
                new PricingItem("lighting_fixed", "Cambio de luminaria", "HABITACIÓN", PricingUnit.FIXED, 90000),

                new PricingItem("margin_percent", "Margen de ganancia (%)", "CONFIG", PricingUnit.PERCENT, 20)));
    }
}
