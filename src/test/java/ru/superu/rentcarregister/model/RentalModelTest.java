package ru.superu.rentcarregister.model;

import org.junit.jupiter.api.Test;
import ru.superu.rentcarregister.model.enums.RentalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class RentalModelTest {

    @Test
    void getPlannedDays_correctCalculation() {
        Rental rental = Rental.builder()
                .plannedStartDate(LocalDate.of(2024, 6, 1))
                .plannedEndDate(LocalDate.of(2024, 6, 8))
                .build();

        assertThat(rental.getPlannedDays()).isEqualTo(7);
    }

    @Test
    void getActualDays_returnsZeroWithoutDates() {
        Rental rental = Rental.builder().build();
        assertThat(rental.getActualDays()).isEqualTo(0);
    }

    @Test
    void defaultStatus_isPending() {
        Rental rental = Rental.builder()
                .plannedStartDate(LocalDate.now())
                .plannedEndDate(LocalDate.now().plusDays(3))
                .dailyRate(BigDecimal.valueOf(1000))
                .build();

        assertThat(rental.getStatus()).isEqualTo(RentalStatus.PENDING);
    }
}
