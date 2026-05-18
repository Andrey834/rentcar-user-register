package ru.superu.rentcarregister.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.superu.rentcarregister.dto.DiscountDto;
import ru.superu.rentcarregister.exception.DiscountNotFoundException;
import ru.superu.rentcarregister.model.Discount;
import ru.superu.rentcarregister.model.enums.DiscountType;
import ru.superu.rentcarregister.repository.DiscountRepository;
import ru.superu.rentcarregister.repository.DriverLicenseRepository;
import ru.superu.rentcarregister.repository.RentalRepository;
import ru.superu.rentcarregister.service.impl.DiscountServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    @Mock
    private DiscountRepository discountRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private DriverLicenseRepository licenseRepository;

    @InjectMocks
    private DiscountServiceImpl discountService;

    private UUID discountId;
    private Discount promoDiscount;

    @BeforeEach
    void setUp() {
        discountId = UUID.randomUUID();
        promoDiscount = Discount.builder()
                .id(discountId)
                .name("SUMMER2024")
                .type(DiscountType.PROMO_CODE)
                .promoCode("SUMMER2024")
                .discountPercent(BigDecimal.valueOf(15))
                .validFrom(LocalDate.now().minusDays(10))
                .validTo(LocalDate.now().plusDays(30))
                .active(true)
                .build();
    }

    @Test
    void createDiscount_savesAndReturnsDto() {
        DiscountDto dto = DiscountDto.builder()
                .name("SUMMER2024")
                .type(DiscountType.PROMO_CODE)
                .promoCode("SUMMER2024")
                .discountPercent(BigDecimal.valueOf(15))
                .validFrom(LocalDate.now().minusDays(10))
                .validTo(LocalDate.now().plusDays(30))
                .build();

        when(discountRepository.save(any(Discount.class))).thenReturn(promoDiscount);

        DiscountDto result = discountService.createDiscount(dto);

        assertThat(result.getName()).isEqualTo("SUMMER2024");
        assertThat(result.getDiscountPercent()).isEqualByComparingTo(BigDecimal.valueOf(15));
        assertThat(result.isActive()).isTrue();
    }

    @Test
    void applyPromoCode_validCode_returnsDiscount() {
        when(discountRepository.findByPromoCode("SUMMER2024")).thenReturn(Optional.of(promoDiscount));

        BigDecimal result = discountService.applyPromoCode("SUMMER2024", UUID.randomUUID(), 7L);

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(15));
    }

    @Test
    void applyPromoCode_invalidCode_throwsException() {
        when(discountRepository.findByPromoCode("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discountService.applyPromoCode("INVALID", UUID.randomUUID(), 7L))
                .isInstanceOf(DiscountNotFoundException.class);
    }

    @Test
    void applyPromoCode_expiredCode_throwsException() {
        promoDiscount.setValidTo(LocalDate.now().minusDays(1));
        when(discountRepository.findByPromoCode("SUMMER2024")).thenReturn(Optional.of(promoDiscount));

        assertThatThrownBy(() -> discountService.applyPromoCode("SUMMER2024", UUID.randomUUID(), 7L))
                .isInstanceOf(DiscountNotFoundException.class)
                .hasMessageContaining("inactive");
    }

    @Test
    void applyPromoCode_belowMinDays_throwsException() {
        promoDiscount.setMinRentalDays(7);
        when(discountRepository.findByPromoCode("SUMMER2024")).thenReturn(Optional.of(promoDiscount));

        assertThatThrownBy(() -> discountService.applyPromoCode("SUMMER2024", UUID.randomUUID(), 3L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("minimum");
    }

    @Test
    void calculateLoyaltyDiscount_noRentals_returnsZero() {
        UUID driverId = UUID.randomUUID();
        when(rentalRepository.countCompletedByDriver(driverId)).thenReturn(0L);
        when(discountRepository.findActiveByTypeAndDate(any(), any())).thenReturn(List.of());

        BigDecimal result = discountService.calculateLoyaltyDiscount(driverId);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void calculateLoyaltyDiscount_withRentals_returnsDiscount() {
        UUID driverId = UUID.randomUUID();
        Discount loyalty = Discount.builder()
                .type(DiscountType.LOYALTY)
                .discountPercent(BigDecimal.valueOf(10))
                .minRentalsRequired(5)
                .active(true)
                .build();

        when(rentalRepository.countCompletedByDriver(driverId)).thenReturn(10L);
        when(discountRepository.findActiveByTypeAndDate(any(), any())).thenReturn(List.of(loyalty));

        BigDecimal result = discountService.calculateLoyaltyDiscount(driverId);

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(10));
    }

    @Test
    void deactivateDiscount_setsActiveToFalse() {
        when(discountRepository.findById(discountId)).thenReturn(Optional.of(promoDiscount));
        when(discountRepository.save(any(Discount.class))).thenAnswer(inv -> inv.getArgument(0));

        discountService.deactivateDiscount(discountId);

        verify(discountRepository).save(argThat(d -> !d.isActive()));
    }

    @Test
    void getAllActiveDiscounts_returnsOnlyActive() {
        when(discountRepository.findByActiveTrue()).thenReturn(List.of(promoDiscount));

        List<DiscountDto> result = discountService.getAllActiveDiscounts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();
    }
}
