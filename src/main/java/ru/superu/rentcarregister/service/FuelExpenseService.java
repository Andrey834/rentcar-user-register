package ru.superu.rentcarregister.service;

import ru.superu.rentcarregister.dto.FuelExpenseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FuelExpenseService {
    FuelExpenseDto addExpense(UUID carId, FuelExpenseDto dto);
    List<FuelExpenseDto> getExpensesByCar(UUID carId);
    List<FuelExpenseDto> getExpensesInPeriod(LocalDate from, LocalDate to);
    void deleteExpense(UUID id);
}
