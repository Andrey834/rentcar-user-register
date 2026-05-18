package ru.superu.rentcarregister.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.superu.rentcarregister.dto.CarExpenseSummaryDto;
import ru.superu.rentcarregister.dto.ReportDto;
import ru.superu.rentcarregister.service.ReportService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/financial")
    public ResponseEntity<ReportDto> getFinancialReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(reportService.getFinancialReport(from, to));
    }

    @GetMapping("/cars/{carId}/summary")
    public ResponseEntity<CarExpenseSummaryDto> getCarSummary(@PathVariable UUID carId) {
        return ResponseEntity.ok(reportService.getCarExpenseSummary(carId));
    }

    @GetMapping("/cars/summary")
    public ResponseEntity<List<CarExpenseSummaryDto>> getAllCarsSummary() {
        return ResponseEntity.ok(reportService.getAllCarsExpenseSummary());
    }
}
