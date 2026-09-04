package com.fiflip.backend.budget.application;

import com.fiflip.backend.budget.domain.RoomType;

import java.util.Set;

public record RoomInput(RoomType type, double largo, double ancho, double altura, Set<String> itemKeys) {

    public RoomInput {
        itemKeys = itemKeys == null ? Set.of() : Set.copyOf(itemKeys);
    }
}
