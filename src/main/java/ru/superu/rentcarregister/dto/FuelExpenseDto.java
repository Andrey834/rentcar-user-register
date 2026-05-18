package ru.superu.rentcarregister.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuelExpenseDto {
    private UUID id;
    private UUID carId;
    private String carInfo;

    @NotNull
    private LocalDate date;

    @NotNull
    @DecimalMin("0.1")
    private BigDecimal liters;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal pricePerLiter;

    private BigDecimal totalCost;

    @Min(0)
    private Integer mileageAtFill;

    @Size(max = 100)
    private String station;

    private LocalDateTime createdAt;
}
