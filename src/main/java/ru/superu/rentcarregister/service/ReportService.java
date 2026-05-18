package ru.superu.rentcarregister.service;

import ru.superu.rentcarregister.dto.CarExpenseSummaryDto;
import ru.superu.rentcarregister.dto.ReportDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReportService {
    ReportDto getFinancialReport(LocalDate from, LocalDate to);
    CarExpenseSummaryDto getCarExpenseSummary(UUID carId);
    List<CarExpenseSummaryDto> getAllCarsExpenseSummary();
}
