package ru.superu.rentcarregister.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.superu.rentcarregister.dto.DiscountDto;
import ru.superu.rentcarregister.service.DiscountService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/discounts")
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping
    public ResponseEntity<DiscountDto> createDiscount(@Valid @RequestBody DiscountDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(discountService.createDiscount(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscountDto> getDiscountById(@PathVariable UUID id) {
        return ResponseEntity.ok(discountService.getDiscountById(id));
    }

    @GetMapping
    public ResponseEntity<List<DiscountDto>> getDiscounts(@RequestParam(defaultValue = "true") boolean activeOnly) {
        if (activeOnly) return ResponseEntity.ok(discountService.getAllActiveDiscounts());
        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiscountDto> updateDiscount(@PathVariable UUID id, @Valid @RequestBody DiscountDto dto) {
        return ResponseEntity.ok(discountService.updateDiscount(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateDiscount(@PathVariable UUID id) {
        discountService.deactivateDiscount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validatePromoCode(
            @RequestParam String promoCode,
            @RequestParam UUID driverId,
            @RequestParam long rentalDays) {
        BigDecimal discount = discountService.applyPromoCode(promoCode, driverId, rentalDays);
        return ResponseEntity.ok(Map.of("promoCode", promoCode, "discountPercent", discount));
    }

    @GetMapping("/loyalty/{driverId}")
    public ResponseEntity<Map<String, Object>> getLoyaltyDiscount(@PathVariable UUID driverId) {
        BigDecimal discount = discountService.calculateLoyaltyDiscount(driverId);
        return ResponseEntity.ok(Map.of("driverId", driverId, "loyaltyDiscountPercent", discount));
    }
}
