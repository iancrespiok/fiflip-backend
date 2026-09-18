package com.fiflip.backend.budget.application;

import java.util.List;

/**
 * Margin-inclusive price per room, in the same order as the input rooms. Each room is rounded
 * to whole pesos and {@code total} is the sum of those rounded values, so a per-room report
 * always adds up to the total shown to the user.
 */
public record BudgetResult(double total, List<Double> roomTotals) {

    public BudgetResult {
        roomTotals = List.copyOf(roomTotals);
    }
}
