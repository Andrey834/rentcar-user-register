package ru.superu.rentcarregister.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.superu.rentcarregister.dto.DiscountDto;
import ru.superu.rentcarregister.exception.DiscountNotFoundException;
import ru.superu.rentcarregister.model.Discount;
import ru.superu.rentcarregister.model.enums.DiscountType;
import ru.superu.rentcarregister.repository.DiscountRepository;
import ru.superu.rentcarregister.repository.DriverLicenseRepository;
import ru.superu.rentcarregister.repository.RentalRepository;
import ru.superu.rentcarregister.service.DiscountService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final RentalRepository rentalRepository;
    private final DriverLicenseRepository licenseRepository;

    @Override
    @Transactional
    public DiscountDto createDiscount(DiscountDto dto) {
        Discount discount = Discount.builder()
                .name(dto.getName())
                .type(dto.getType())
                .promoCode(dto.getPromoCode() != null ? dto.getPromoCode().toUpperCase() : null)
                .discountPercent(dto.getDiscountPercent())
                .validFrom(dto.getValidFrom())
                .validTo(dto.getValidTo())
                .minRentalsRequired(dto.getMinRentalsRequired())
                .minRentalDays(dto.getMinRentalDays())
                .active(true)
                .description(dto.getDescription())
                .build();
        Discount saved = discountRepository.save(discount);
        log.info("Discount created: {} ({}%)", saved.getName(), saved.getDiscountPercent());
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountDto getDiscountById(UUID id) {
        return toDto(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountDto> getAllActiveDiscounts() {
        return discountRepository.findByActiveTrue().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountDto> getAllDiscounts() {
        return discountRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public DiscountDto updateDiscount(UUID id, DiscountDto dto) {
        Discount discount = findById(id);
        discount.setName(dto.getName());
        discount.setDiscountPercent(dto.getDiscountPercent());
        discount.setValidFrom(dto.getValidFrom());
        discount.setValidTo(dto.getValidTo());
        discount.setMinRentalsRequired(dto.getMinRentalsRequired());
        discount.setMinRentalDays(dto.getMinRentalDays());
        discount.setDescription(dto.getDescription());
        return toDto(discountRepository.save(discount));
    }

    @Override
    @Transactional
    public void deactivateDiscount(UUID id) {
        Discount discount = findById(id);
        discount.setActive(false);
        discountRepository.save(discount);
        log.info("Discount deactivated: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal applyPromoCode(String promoCode, UUID driverId, long rentalDays) {
        Discount discount = discountRepository.findByPromoCode(promoCode.toUpperCase())
                .orElseThrow(() -> new DiscountNotFoundException("Promo code not found: " + promoCode));

        if (!discount.isApplicable(LocalDate.now())) {
            throw new DiscountNotFoundException("Promo code is expired or inactive: " + promoCode);
        }
        if (discount.getMinRentalDays() != null && rentalDays < discount.getMinRentalDays()) {
            throw new IllegalStateException(
                    "Promo code requires minimum " + discount.getMinRentalDays() + " rental days");
        }
        log.info("Promo code applied: {} ({}%)", promoCode, discount.getDiscountPercent());
        return discount.getDiscountPercent();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateLoyaltyDiscount(UUID driverId) {
        long completedRentals = rentalRepository.countCompletedByDriver(driverId);

        List<Discount> loyaltyDiscounts = discountRepository
                .findActiveByTypeAndDate(DiscountType.LOYALTY, LocalDate.now());

        return loyaltyDiscounts.stream()
                .filter(d -> d.getMinRentalsRequired() == null
                             || completedRentals >= d.getMinRentalsRequired())
                .map(Discount::getDiscountPercent)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private Discount findById(UUID id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new DiscountNotFoundException("Discount not found: " + id));
    }

    private DiscountDto toDto(Discount d) {
        return DiscountDto.builder()
                .id(d.getId())
                .name(d.getName())
                .type(d.getType())
                .promoCode(d.getPromoCode())
                .discountPercent(d.getDiscountPercent())
                .validFrom(d.getValidFrom())
                .validTo(d.getValidTo())
                .minRentalsRequired(d.getMinRentalsRequired())
                .minRentalDays(d.getMinRentalDays())
                .active(d.isActive())
                .description(d.getDescription())
                .createdAt(d.getCreatedAt())
                .build();
    }
}
