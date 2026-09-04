package com.fiflip.backend.budget.application;

import java.util.List;

public interface BudgetCalculationUseCases {
    double calculateTotal(List<RoomInput> rooms);
}
