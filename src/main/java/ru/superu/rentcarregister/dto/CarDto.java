package ru.superu.rentcarregister.dto;

import lombok.*;
import ru.superu.rentcarregister.model.enums.CarCategory;
import ru.superu.rentcarregister.model.enums.CarStatus;
import ru.superu.rentcarregister.model.enums.FuelType;
import ru.superu.rentcarregister.model.enums.Transmission;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarDto {
    private UUID id;
    private String brand;
    private String model;
    private Integer year;
    private String licensePlate;
    private String vin;
    private CarCategory category;
    private CarStatus status;
    private Integer currentMileage;
    private FuelType fuelType;
    private BigDecimal fuelConsumptionPer100km;
    private BigDecimal dailyRate;
    private String color;
    private Integer seats;
    private Transmission transmission;
    private LocalDate insuranceExpireDate;
    private Integer nextMaintenanceMileage;
    private String description;
    private LocalDateTime createdAt;
}
