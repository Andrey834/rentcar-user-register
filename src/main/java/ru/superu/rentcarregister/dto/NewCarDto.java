package ru.superu.rentcarregister.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import ru.superu.rentcarregister.model.enums.CarCategory;
import ru.superu.rentcarregister.model.enums.FuelType;
import ru.superu.rentcarregister.model.enums.Transmission;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCarDto {

    @NotBlank
    @Size(max = 50)
    private String brand;

    @NotBlank
    @Size(max = 50)
    private String model;

    @NotNull
    @Min(1900)
    private Integer year;

    @NotBlank
    @Size(max = 20)
    private String licensePlate;

    @Size(max = 17)
    private String vin;

    @NotNull
    private CarCategory category;

    @NotNull
    @DecimalMin("0")
    private Integer currentMileage;

    @NotNull
    private FuelType fuelType;

    @NotNull
    @DecimalMin("0.1")
    private BigDecimal fuelConsumptionPer100km;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal dailyRate;

    @Size(max = 30)
    private String color;

    @Min(1)
    @Max(50)
    private Integer seats;

    private Transmission transmission;

    private LocalDate insuranceExpireDate;

    private Integer nextMaintenanceMileage;

    @Size(max = 500)
    private String description;
}
