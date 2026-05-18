package ru.superu.rentcarregister.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.superu.rentcarregister.dto.CompleteRentalDto;
import ru.superu.rentcarregister.dto.NewRentalDto;
import ru.superu.rentcarregister.dto.RentalDto;
import ru.superu.rentcarregister.model.enums.RentalStatus;
import ru.superu.rentcarregister.service.RentalService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rentals")
public class RentalController {

    private final RentalService rentalService;

    @PostMapping
    public ResponseEntity<RentalDto> createRental(@Valid @RequestBody NewRentalDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rentalService.createRental(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalDto> getRental(@PathVariable UUID id) {
        return ResponseEntity.ok(rentalService.getRentalById(id));
    }

    @GetMapping
    public ResponseEntity<List<RentalDto>> getRentals(
            @RequestParam(required = false) RentalStatus status,
            @RequestParam(required = false) UUID driverId,
            @RequestParam(required = false) UUID carId) {
        if (driverId != null) return ResponseEntity.ok(rentalService.getRentalsByDriver(driverId));
        if (carId != null) return ResponseEntity.ok(rentalService.getRentalsByCar(carId));
        if (status != null) return ResponseEntity.ok(rentalService.getRentalsByStatus(status));
        return ResponseEntity.ok(rentalService.getRentalsByStatus(RentalStatus.PENDING));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<RentalDto>> getOverdueRentals() {
        return ResponseEntity.ok(rentalService.getOverdueRentals());
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<RentalDto> startRental(@PathVariable UUID id,
                                                 @RequestParam Integer startMileage,
                                                 @RequestParam BigDecimal startFuelPercent) {
        return ResponseEntity.ok(rentalService.startRental(id, startMileage, startFuelPercent));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<RentalDto> completeRental(@PathVariable UUID id,
                                                    @Valid @RequestBody CompleteRentalDto dto) {
        return ResponseEntity.ok(rentalService.completeRental(id, dto));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<RentalDto> cancelRental(@PathVariable UUID id) {
        return ResponseEntity.ok(rentalService.cancelRental(id));
    }
}
