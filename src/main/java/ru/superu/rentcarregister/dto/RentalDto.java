package ru.superu.rentcarregister.dto;

import lombok.*;
import ru.superu.rentcarregister.model.enums.RentalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalDto {
    private UUID id;
    private UUID driverId;
    private String driverName;
    private UUID carId;
    private String carInfo;
    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private LocalDate actualStartDate;
    private LocalDate actualReturnDate;
    private Integer startMileage;
    private Integer endMileage;
    private BigDecimal startFuelPercent;
    private BigDecimal endFuelPercent;
    private RentalStatus status;
    private BigDecimal dailyRate;
    private BigDecimal discountPercent;
    private BigDecimal totalAmount;
    private BigDecimal deposit;
    private BigDecimal fuelCompensation;
    private long plannedDays;
    private String notes;
    private LocalDateTime createdAt;
}
