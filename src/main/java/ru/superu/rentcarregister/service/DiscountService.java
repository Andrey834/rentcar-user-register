package ru.superu.rentcarregister.service;

import ru.superu.rentcarregister.dto.DiscountDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface DiscountService {
    DiscountDto createDiscount(DiscountDto dto);
    DiscountDto getDiscountById(UUID id);
    List<DiscountDto> getAllActiveDiscounts();
    List<DiscountDto> getAllDiscounts();
    DiscountDto updateDiscount(UUID id, DiscountDto dto);
    void deactivateDiscount(UUID id);
    BigDecimal applyPromoCode(String promoCode, UUID driverId, long rentalDays);
    BigDecimal calculateLoyaltyDiscount(UUID driverId);
}
