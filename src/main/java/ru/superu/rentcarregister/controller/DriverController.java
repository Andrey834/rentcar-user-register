package ru.superu.rentcarregister.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.superu.rentcarregister.dto.DriverLicenseDto;
import ru.superu.rentcarregister.dto.NewDriverLicenseDto;
import ru.superu.rentcarregister.service.DriverService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/license")
    public ResponseEntity<DriverLicenseDto> registerLicense(@Valid @RequestBody NewDriverLicenseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.registerLicense(dto));
    }

    @GetMapping("/{personId}/license")
    public ResponseEntity<DriverLicenseDto> getLicenseByPerson(@PathVariable UUID personId) {
        return ResponseEntity.ok(driverService.getLicenseByPersonId(personId));
    }

    @GetMapping("/license/{licenseId}")
    public ResponseEntity<DriverLicenseDto> getLicenseById(@PathVariable UUID licenseId) {
        return ResponseEntity.ok(driverService.getLicenseById(licenseId));
    }

    @GetMapping
    public ResponseEntity<List<DriverLicenseDto>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllLicenses());
    }

    @PutMapping("/license/{licenseId}")
    public ResponseEntity<DriverLicenseDto> updateLicense(@PathVariable UUID licenseId,
                                                          @Valid @RequestBody NewDriverLicenseDto dto) {
        return ResponseEntity.ok(driverService.updateLicense(licenseId, dto));
    }
}
