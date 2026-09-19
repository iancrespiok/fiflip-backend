package com.fiflip.backend.budget.domain;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Which pricing formula applies to each checklist item, per room type — a framework-free
 * port of the frontend's QUESTIONS config (key -> formula only, no labels; those stay
 * frontend-only for the UI). Hardcoded on purpose, same as the paint coverage constants:
 * adding a checkbox already requires a frontend code change, so this doesn't add any new
 * rigidity, and it keeps the pricing formula out of anything a client could tamper with.
 */
public final class RoomItemCatalog {

    private RoomItemCatalog() {
    }

    private static final Map<String, ItemPricing> BANO = Map.ofEntries(
            Map.entry("sanitarios", new ItemPricing.FixedSplit("sanitary_material_fixed", "sanitary_labor_fixed")),
            Map.entry("techo", new ItemPricing.AreaSplit("ceiling_gypsum_material_m2", "ceiling_gypsum_labor_m2", Surface.FLOOR)),
            Map.entry("revestimientos", new ItemPricing.AreaSplit("wall_covering_material_m2", "wall_covering_labor_m2", Surface.WALL)),
            Map.entry("ducha", new ItemPricing.FixedSplit("shower_glass_material_fixed", "shower_glass_labor_fixed")),
            Map.entry("enchufes", new ItemPricing.FixedSplit("outlets_material_fixed", "outlets_labor_fixed")),
            Map.entry("vanitory", new ItemPricing.FixedSplit("vanity_mirror_material_fixed", "vanity_mirror_labor_fixed")),
            Map.entry("griferias", new ItemPricing.FixedSplit("faucet_material_fixed", "faucet_labor_fixed")),
            Map.entry("abertura", new ItemPricing.FixedSplit("door_window_material_fixed", "door_window_labor_fixed")),
            Map.entry("puerta_corrediza", new ItemPricing.FixedSplit("sliding_door_material_fixed", "sliding_door_labor_fixed")));

    private static final Map<String, ItemPricing> COCINA = Map.ofEntries(
            Map.entry("ampliar", new ItemPricing.FixedSplit("kitchen_expand_material_fixed", "kitchen_expand_labor_fixed")),
            Map.entry("muebles", new ItemPricing.FixedSplit("kitchen_furniture_material_fixed", "kitchen_furniture_labor_fixed")),
            Map.entry("revestimientos", new ItemPricing.AreaSplit("wall_covering_material_m2", "wall_covering_labor_m2", Surface.WALL)),
            Map.entry("griferias", new ItemPricing.FixedSplit("faucet_material_fixed", "faucet_labor_fixed")),
            Map.entry("mesadas", new ItemPricing.FixedSplit("countertop_material_fixed", "countertop_labor_fixed")),
            Map.entry("enchufes", new ItemPricing.FixedSplit("outlets_material_fixed", "outlets_labor_fixed")),
            Map.entry("techo", new ItemPricing.AreaSplit("ceiling_gypsum_material_m2", "ceiling_gypsum_labor_m2", Surface.FLOOR)),
            Map.entry("pintar", new ItemPricing.Paint(true)),
            Map.entry("abertura", new ItemPricing.FixedSplit("door_window_material_fixed", "door_window_labor_fixed")),
            Map.entry("aire", new ItemPricing.FixedSplit("ac_material_fixed", "ac_labor_fixed")));

    private static final Map<String, ItemPricing> HABITACION = Map.ofEntries(
            Map.entry("pintar", new ItemPricing.Paint(true)),
            Map.entry("pisos", new ItemPricing.AreaSplit("floor_material_m2", "floor_labor_m2", Surface.FLOOR)),
            Map.entry("placar", new ItemPricing.FixedSplit("closet_doors_material_fixed", "closet_doors_labor_fixed")),
            Map.entry("luminaria", new ItemPricing.FixedSplit("lighting_material_fixed", "lighting_labor_fixed")),
            Map.entry("aire", new ItemPricing.FixedSplit("ac_material_fixed", "ac_labor_fixed")),
            Map.entry("abertura", new ItemPricing.FixedSplit("door_window_material_fixed", "door_window_labor_fixed")));

    // Pasillo o distribuidor: lo mismo que una habitación (mismos precios), pero sin aire
    // acondicionado ni aberturas.
    private static final Set<String> NOT_IN_PASILLO = Set.of("aire", "abertura");
    private static final Map<String, ItemPricing> PASILLO = HABITACION.entrySet().stream()
            .filter(entry -> !NOT_IN_PASILLO.contains(entry.getKey()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

    // Espacio exterior (patio, balcón, terraza). Se guarda como TERRAZA: es el nombre interno del
    // tipo, no lo que ve el usuario. Pintar se calcula sobre las paredes (no hay techo que pintar):
    // el frontend arranca este tipo con 1,5 m de altura.
    private static final Map<String, ItemPricing> TERRAZA = Map.ofEntries(
            Map.entry("pintar", new ItemPricing.Paint(false)),
            Map.entry("pisos", new ItemPricing.AreaSplit("floor_material_m2", "floor_labor_m2", Surface.FLOOR)),
            Map.entry("impermeabilizar", new ItemPricing.AreaSplit("waterproofing_material_m2", "waterproofing_labor_m2", Surface.FLOOR)),
            Map.entry("luminaria", new ItemPricing.FixedSplit("lighting_material_fixed", "lighting_labor_fixed")),
            Map.entry("tanque", new ItemPricing.FixedSplit("water_tank_material_fixed", "water_tank_labor_fixed")));

    private static final Map<RoomType, Map<String, ItemPricing>> BY_TYPE = Map.of(
            RoomType.BANO, BANO,
            RoomType.COCINA, COCINA,
            RoomType.HABITACION, HABITACION,
            RoomType.PASILLO, PASILLO,
            RoomType.TERRAZA, TERRAZA);

    public static Map<String, ItemPricing> itemsFor(RoomType type) {
        return BY_TYPE.getOrDefault(type, Map.of());
    }
}
