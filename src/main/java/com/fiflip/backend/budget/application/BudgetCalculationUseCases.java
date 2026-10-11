package com.fiflip.backend.budget.application;

import java.util.List;

public interface BudgetCalculationUseCases {
    BudgetResult calculate(List<RoomInput> rooms);

    /** Same as {@link #calculate} but without {@code margin_percent}: the real cost of the work. */
    BudgetResult calculateCost(List<RoomInput> rooms);
}
