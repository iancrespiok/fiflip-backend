package com.fiflip.backend.budget.domain;

import java.util.Map;

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
            Map.entry("pintar", new ItemPricing.Paint()),
            Map.entry("abertura", new ItemPricing.FixedSplit("door_window_material_fixed", "door_window_labor_fixed")),
            Map.entry("aire", new ItemPricing.FixedSplit("ac_material_fixed", "ac_labor_fixed")));

    private static final Map<String, ItemPricing> HABITACION = Map.ofEntries(
            Map.entry("pintar", new ItemPricing.Paint()),
            Map.entry("pisos", new ItemPricing.AreaSplit("floor_material_m2", "floor_labor_m2", Surface.FLOOR)),
            Map.entry("placar", new ItemPricing.FixedSplit("closet_doors_material_fixed", "closet_doors_labor_fixed")),
            Map.entry("luminaria", new ItemPricing.FixedSplit("lighting_material_fixed", "lighting_labor_fixed")),
            Map.entry("aire", new ItemPricing.FixedSplit("ac_material_fixed", "ac_labor_fixed")),
            Map.entry("abertura", new ItemPricing.FixedSplit("door_window_material_fixed", "door_window_labor_fixed")));

    private static final Map<RoomType, Map<String, ItemPricing>> BY_TYPE = Map.of(
            RoomType.BANO, BANO,
            RoomType.COCINA, COCINA,
            RoomType.HABITACION, HABITACION);

    public static Map<String, ItemPricing> itemsFor(RoomType type) {
        return BY_TYPE.getOrDefault(type, Map.of());
    }
}
