package ru.superu.rentcarregister.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.superu.rentcarregister.dto.FuelExpenseDto;
import ru.superu.rentcarregister.service.FuelExpenseService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fuel-expenses")
public class FuelExpenseController {

    private final FuelExpenseService fuelExpenseService;

    @PostMapping("/cars/{carId}")
    public ResponseEntity<FuelExpenseDto> addExpense(@PathVariable UUID carId,
                                                     @Valid @RequestBody FuelExpenseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fuelExpenseService.addExpense(carId, dto));
    }

    @GetMapping("/cars/{carId}")
    public ResponseEntity<List<FuelExpenseDto>> getExpensesByCar(@PathVariable UUID carId) {
        return ResponseEntity.ok(fuelExpenseService.getExpensesByCar(carId));
    }

    @GetMapping
    public ResponseEntity<List<FuelExpenseDto>> getExpensesInPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(fuelExpenseService.getExpensesInPeriod(from, to));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable UUID id) {
        fuelExpenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
