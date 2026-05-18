package ru.superu.rentcarregister.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import ru.superu.rentcarregister.model.enums.MaintenanceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceRecordDto {
    private UUID id;
    private UUID carId;
    private String carInfo;

    @NotNull
    private LocalDate date;

    @Min(0)
    private Integer mileageAtService;

    @NotNull
    private MaintenanceType type;

    @Size(max = 1000)
    private String description;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal cost;

    @Size(max = 200)
    private String provider;

    private Integer nextServiceMileage;

    private LocalDate nextServiceDate;

    private LocalDateTime createdAt;
}
