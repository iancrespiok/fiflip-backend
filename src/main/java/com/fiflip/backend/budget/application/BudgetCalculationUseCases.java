package com.fiflip.backend.budget.application;

import java.util.List;

public interface BudgetCalculationUseCases {
    BudgetResult calculate(List<RoomInput> rooms);
}
