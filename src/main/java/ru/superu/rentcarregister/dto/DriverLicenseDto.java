package ru.superu.rentcarregister.dto;

import lombok.*;
import ru.superu.rentcarregister.model.enums.LicenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverLicenseDto {
    private UUID id;
    private UUID personId;
    private String driverName;
    private String licenseNumber;
    private LocalDate issueDate;
    private LocalDate expireDate;
    private LicenseCategory category;
    private Integer experienceYears;
    private BigDecimal rating;
    private Integer completedRentals;
    private boolean valid;
    private LocalDateTime createdAt;
}
