package ru.superu.rentcarregister.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.superu.rentcarregister.dto.MaintenanceRecordDto;
import ru.superu.rentcarregister.model.enums.MaintenanceType;
import ru.superu.rentcarregister.service.MaintenanceService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping("/cars/{carId}")
    public ResponseEntity<MaintenanceRecordDto> addRecord(@PathVariable UUID carId,
                                                          @Valid @RequestBody MaintenanceRecordDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenanceService.addRecord(carId, dto));
    }

    @GetMapping("/cars/{carId}")
    public ResponseEntity<List<MaintenanceRecordDto>> getRecordsByCar(
            @PathVariable UUID carId,
            @RequestParam(required = false) MaintenanceType type) {
        if (type != null) return ResponseEntity.ok(maintenanceService.getRecordsByCarAndType(carId, type));
        return ResponseEntity.ok(maintenanceService.getRecordsByCar(carId));
    }

    @GetMapping
    public ResponseEntity<List<MaintenanceRecordDto>> getRecordsInPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(maintenanceService.getRecordsInPeriod(from, to));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable UUID id) {
        maintenanceService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
