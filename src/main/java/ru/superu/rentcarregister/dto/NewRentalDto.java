package ru.superu.rentcarregister.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewRentalDto {

    @NotNull
    private UUID driverId;

    @NotNull
    private UUID carId;

    @NotNull
    @FutureOrPresent
    private LocalDate plannedStartDate;

    @NotNull
    @Future
    private LocalDate plannedEndDate;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal startFuelPercent;

    private String promoCode;

    @Size(max = 1000)
    private String notes;
}
