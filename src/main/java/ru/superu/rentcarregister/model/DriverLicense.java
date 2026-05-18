package ru.superu.rentcarregister.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.superu.rentcarregister.model.enums.LicenseCategory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "driver_licenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class DriverLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", unique = true, nullable = false)
    private Person person;

    @NotBlank
    @Size(max = 20)
    @Column(length = 20, unique = true, nullable = false)
    private String licenseNumber;

    @NotNull
    private LocalDate issueDate;

    @NotNull
    private LocalDate expireDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LicenseCategory category;

    @Min(0)
    @Max(100)
    private Integer experienceYears;

    @DecimalMin("0.0")
    @DecimalMax("5.0")
    @Builder.Default
    @Column(precision = 3, scale = 2)
    private java.math.BigDecimal rating = java.math.BigDecimal.valueOf(5.0);

    @Builder.Default
    private Integer completedRentals = 0;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isValid() {
        return expireDate != null && expireDate.isAfter(LocalDate.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DriverLicense that = (DriverLicense) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
