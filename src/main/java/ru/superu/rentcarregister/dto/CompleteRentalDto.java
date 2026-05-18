package ru.superu.rentcarregister.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteRentalDto {

    @NotNull
    @Min(0)
    private Integer endMileage;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal endFuelPercent;

    @Size(max = 1000)
    private String notes;
}
