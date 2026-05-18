package ru.superu.rentcarregister.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.superu.rentcarregister.dto.CarDto;
import ru.superu.rentcarregister.dto.NewCarDto;
import ru.superu.rentcarregister.model.enums.CarCategory;
import ru.superu.rentcarregister.model.enums.CarStatus;
import ru.superu.rentcarregister.service.CarService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cars")
public class CarController {

    private final CarService carService;

    @PostMapping
    public ResponseEntity<CarDto> addCar(@Valid @RequestBody NewCarDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.addCar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDto> getCarById(@PathVariable UUID id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @GetMapping
    public ResponseEntity<List<CarDto>> getAllCars(
            @RequestParam(required = false) CarStatus status,
            @RequestParam(required = false) CarCategory category) {
        if (status != null) return ResponseEntity.ok(carService.getCarsByStatus(status));
        if (category != null) return ResponseEntity.ok(carService.getCarsByCategory(category));
        return ResponseEntity.ok(carService.getAllCars());
    }

    @GetMapping("/available")
    public ResponseEntity<List<CarDto>> getAvailableCars(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(carService.getAvailableCarsForPeriod(startDate, endDate));
    }

    @GetMapping("/maintenance-needed")
    public ResponseEntity<List<CarDto>> getCarsNeedingMaintenance() {
        return ResponseEntity.ok(carService.getCarsNeedingMaintenance());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarDto> updateCar(@PathVariable UUID id, @Valid @RequestBody NewCarDto dto) {
        return ResponseEntity.ok(carService.updateCar(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CarDto> updateStatus(@PathVariable UUID id, @RequestParam CarStatus status) {
        return ResponseEntity.ok(carService.updateCarStatus(id, status));
    }

    @PatchMapping("/{id}/mileage")
    public ResponseEntity<CarDto> updateMileage(@PathVariable UUID id, @RequestParam Integer mileage) {
        return ResponseEntity.ok(carService.updateMileage(id, mileage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable UUID id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
