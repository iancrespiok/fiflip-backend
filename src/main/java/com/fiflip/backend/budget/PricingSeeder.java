package com.fiflip.backend.budget;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Keeps the pricing_items catalog in sync with the current code on every
 * boot: removes keys that were replaced by a finer-grained split (e.g. the
 * old single "wall_covering_m2" after splitting into material+labor), and
 * inserts any new key that doesn't exist yet — without touching values an
 * admin already edited for keys that still exist. Prices here are
 * placeholders and must be reviewed in /admin.
 */
@Component
public class PricingSeeder implements CommandLineRunner {

    private final PricingItemRepository repository;
    private final JdbcTemplate jdbcTemplate;

    public PricingSeeder(PricingItemRepository repository, JdbcTemplate jdbcTemplate) {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final List<String> DEPRECATED_KEYS = List.of(
            "wall_covering_m2", "floor_m2", "ceiling_gypsum_m2", "paint_m2",
            "sanitary_fixed", "shower_glass_fixed", "vanity_mirror_fixed", "outlets_fixed",
            "door_window_fixed", "ac_fixed", "kitchen_expand_fixed", "kitchen_furniture_fixed",
            "faucet_fixed", "countertop_fixed", "closet_doors_fixed", "lighting_fixed");

    private static final List<PricingItem> CATALOG = List.of(
            // GENERAL — piso, revestimientos y techo: material y mano de obra por separado
            new PricingItem("floor_material_m2", "Piso — cerámico/porcelanato (material) — por m²", "GENERAL", PricingUnit.M2, 27000),
            new PricingItem("floor_labor_m2", "Piso — colocación (mano de obra) — por m²", "GENERAL", PricingUnit.M2, 18000),
            new PricingItem("wall_covering_material_m2", "Revestimientos — cerámico (material) — por m²", "GENERAL", PricingUnit.M2, 25000),
            new PricingItem("wall_covering_labor_m2", "Revestimientos — colocación (mano de obra) — por m²", "GENERAL", PricingUnit.M2, 17000),
            new PricingItem("ceiling_gypsum_material_m2", "Cielorraso placas de yeso + dicroicos (material) — por m²", "GENERAL", PricingUnit.M2, 23000),
            new PricingItem("ceiling_gypsum_labor_m2", "Cielorraso placas de yeso + dicroicos (mano de obra) — por m²", "GENERAL", PricingUnit.M2, 15000),
            new PricingItem("door_window_material_fixed", "Abertura (material)", "GENERAL", PricingUnit.FIXED, 120000),
            new PricingItem("door_window_labor_fixed", "Abertura (mano de obra)", "GENERAL", PricingUnit.FIXED, 60000),
            new PricingItem("outlets_material_fixed", "Enchufes (material)", "GENERAL", PricingUnit.FIXED, 20000),
            new PricingItem("outlets_labor_fixed", "Enchufes (mano de obra)", "GENERAL", PricingUnit.FIXED, 15000),
            new PricingItem("ac_material_fixed", "Aire acondicionado (material/equipo)", "GENERAL", PricingUnit.FIXED, 500000),
            new PricingItem("ac_labor_fixed", "Aire acondicionado (instalación)", "GENERAL", PricingUnit.FIXED, 150000),

            // PINTURA — mano de obra por m² + materiales calculados por rendimiento
            new PricingItem("paint_labor_m2", "Pintura — mano de obra — por m²", "PINTURA", PricingUnit.M2, 6000),
            new PricingItem("paint_bucket_price", "Balde de pintura látex 20L (precio)", "PINTURA", PricingUnit.FIXED, 85000),
            new PricingItem("paint_bucket_coverage_m2", "Rendimiento balde de pintura 20L", "PINTURA", PricingUnit.COVERAGE_M2, 40),
            new PricingItem("putty_bucket_price", "Balde de enduido 20L (precio)", "PINTURA", PricingUnit.FIXED, 55000),
            new PricingItem("putty_bucket_coverage_m2", "Rendimiento balde de enduido 20L", "PINTURA", PricingUnit.COVERAGE_M2, 25),
            new PricingItem("primer_price", "Fijador 4L (precio)", "PINTURA", PricingUnit.FIXED, 30000),
            new PricingItem("primer_coverage_m2", "Rendimiento fijador 4L", "PINTURA", PricingUnit.COVERAGE_M2, 40),

            // BAÑO
            new PricingItem("sanitary_material_fixed", "Sanitarios — inodoro/bidet (material)", "BAÑO", PricingUnit.FIXED, 140000),
            new PricingItem("sanitary_labor_fixed", "Sanitarios — inodoro/bidet (mano de obra)", "BAÑO", PricingUnit.FIXED, 80000),
            new PricingItem("shower_glass_material_fixed", "Ducha con mampara de vidrio (material)", "BAÑO", PricingUnit.FIXED, 320000),
            new PricingItem("shower_glass_labor_fixed", "Ducha con mampara de vidrio (mano de obra)", "BAÑO", PricingUnit.FIXED, 160000),
            new PricingItem("vanity_mirror_material_fixed", "Vanitory con espejo (material)", "BAÑO", PricingUnit.FIXED, 170000),
            new PricingItem("vanity_mirror_labor_fixed", "Vanitory con espejo (mano de obra)", "BAÑO", PricingUnit.FIXED, 90000),

            // COCINA
            new PricingItem("kitchen_expand_material_fixed", "Ampliación del espacio (material)", "COCINA", PricingUnit.FIXED, 400000),
            new PricingItem("kitchen_expand_labor_fixed", "Ampliación del espacio (mano de obra)", "COCINA", PricingUnit.FIXED, 500000),
            new PricingItem("kitchen_furniture_material_fixed", "Muebles de cocina (material)", "COCINA", PricingUnit.FIXED, 650000),
            new PricingItem("kitchen_furniture_labor_fixed", "Muebles de cocina (mano de obra)", "COCINA", PricingUnit.FIXED, 200000),
            new PricingItem("faucet_material_fixed", "Griferías (material)", "COCINA", PricingUnit.FIXED, 80000),
            new PricingItem("faucet_labor_fixed", "Griferías (mano de obra)", "COCINA", PricingUnit.FIXED, 40000),
            new PricingItem("countertop_material_fixed", "Mesadas (material)", "COCINA", PricingUnit.FIXED, 280000),
            new PricingItem("countertop_labor_fixed", "Mesadas (mano de obra)", "COCINA", PricingUnit.FIXED, 100000),

            // HABITACIÓN
            new PricingItem("closet_doors_material_fixed", "Puertas de placar (material)", "HABITACIÓN", PricingUnit.FIXED, 100000),
            new PricingItem("closet_doors_labor_fixed", "Puertas de placar (mano de obra)", "HABITACIÓN", PricingUnit.FIXED, 50000),
            new PricingItem("lighting_material_fixed", "Luminaria (material)", "HABITACIÓN", PricingUnit.FIXED, 60000),
            new PricingItem("lighting_labor_fixed", "Luminaria (mano de obra)", "HABITACIÓN", PricingUnit.FIXED, 30000),

            // CONFIG
            new PricingItem("margin_percent", "Margen de ganancia (%)", "CONFIG", PricingUnit.PERCENT, 20));

    @Override
    public void run(String... args) {
        // Hibernate's ddl-auto=update never widens a CHECK constraint it generated for an
        // @Enumerated(STRING) column, so adding a new PricingUnit value would make every
        // insert using it fail against the constraint from whenever the table was first
        // created. Drop it — Bean Validation / the enum type itself already guard the values.
        jdbcTemplate.execute("ALTER TABLE pricing_items DROP CONSTRAINT IF EXISTS pricing_items_unit_check");

        DEPRECATED_KEYS.forEach(key -> repository.findById(key).ifPresent(repository::delete));

        for (PricingItem item : CATALOG) {
            if (repository.existsById(item.getKey())) {
                continue;
            }
            repository.save(item);
        }
    }
}
