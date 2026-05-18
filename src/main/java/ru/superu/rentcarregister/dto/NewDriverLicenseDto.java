package ru.superu.rentcarregister.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import ru.superu.rentcarregister.model.enums.LicenseCategory;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewDriverLicenseDto {

    @NotNull
    private UUID personId;

    @NotBlank
    @Size(max = 20)
    private String licenseNumber;

    @NotNull
    private LocalDate issueDate;

    @NotNull
    private LocalDate expireDate;

    @NotNull
    private LicenseCategory category;

    @Min(0)
    @Max(100)
    private Integer experienceYears;
}
