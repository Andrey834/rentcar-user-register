package ru.superu.rentcarregister.model;

import org.junit.jupiter.api.Test;
import ru.superu.rentcarregister.model.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class DiscountModelTest {

    @Test
    void isApplicable_activeWithinDateRange_returnsTrue() {
        Discount discount = Discount.builder()
                .active(true)
                .validFrom(LocalDate.now().minusDays(5))
                .validTo(LocalDate.now().plusDays(5))
                .discountPercent(BigDecimal.TEN)
                .type(DiscountType.SEASONAL)
                .build();

        assertThat(discount.isApplicable(LocalDate.now())).isTrue();
    }

    @Test
    void isApplicable_inactive_returnsFalse() {
        Discount discount = Discount.builder()
                .active(false)
                .discountPercent(BigDecimal.TEN)
                .type(DiscountType.PROMO_CODE)
                .build();

        assertThat(discount.isApplicable(LocalDate.now())).isFalse();
    }

    @Test
    void isApplicable_beforeValidFrom_returnsFalse() {
        Discount discount = Discount.builder()
                .active(true)
                .validFrom(LocalDate.now().plusDays(3))
                .validTo(LocalDate.now().plusDays(10))
                .discountPercent(BigDecimal.TEN)
                .type(DiscountType.SEASONAL)
                .build();

        assertThat(discount.isApplicable(LocalDate.now())).isFalse();
    }

    @Test
    void isApplicable_afterValidTo_returnsFalse() {
        Discount discount = Discount.builder()
                .active(true)
                .validFrom(LocalDate.now().minusDays(10))
                .validTo(LocalDate.now().minusDays(1))
                .discountPercent(BigDecimal.TEN)
                .type(DiscountType.PROMO_CODE)
                .build();

        assertThat(discount.isApplicable(LocalDate.now())).isFalse();
    }

    @Test
    void isApplicable_noDateRange_returnsTrue() {
        Discount discount = Discount.builder()
                .active(true)
                .discountPercent(BigDecimal.valueOf(5))
                .type(DiscountType.LOYALTY)
                .build();

        assertThat(discount.isApplicable(LocalDate.now())).isTrue();
    }
}
