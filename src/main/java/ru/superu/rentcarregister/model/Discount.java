package ru.superu.rentcarregister.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.superu.rentcarregister.model.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "discounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 100)
    @Column(length = 100, nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DiscountType type;

    @Size(max = 50)
    @Column(length = 50, unique = true)
    private String promoCode;

    @NotNull
    @DecimalMin("0.1")
    @DecimalMax("100.0")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercent;

    private LocalDate validFrom;

    private LocalDate validTo;

    @Min(0)
    private Integer minRentalsRequired;

    @Min(1)
    private Integer minRentalDays;

    @Builder.Default
    private boolean active = true;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isApplicable(LocalDate date) {
        if (!active) return false;
        if (validFrom != null && date.isBefore(validFrom)) return false;
        if (validTo != null && date.isAfter(validTo)) return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Discount discount = (Discount) o;
        return id != null && id.equals(discount.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
