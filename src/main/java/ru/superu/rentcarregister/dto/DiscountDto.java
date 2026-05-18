package ru.superu.rentcarregister.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import ru.superu.rentcarregister.model.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountDto {
    private UUID id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private DiscountType type;

    @Size(max = 50)
    private String promoCode;

    @NotNull
    @DecimalMin("0.1")
    @DecimalMax("100.0")
    private BigDecimal discountPercent;

    private LocalDate validFrom;
    private LocalDate validTo;

    @Min(0)
    private Integer minRentalsRequired;

    @Min(1)
    private Integer minRentalDays;

    private boolean active;

    @Size(max = 500)
    private String description;

    private LocalDateTime createdAt;
}
