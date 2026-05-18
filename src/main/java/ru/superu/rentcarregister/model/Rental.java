package ru.superu.rentcarregister.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.superu.rentcarregister.model.enums.RentalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rentals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = {"driver", "car"})
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Person driver;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @NotNull
    private LocalDate plannedStartDate;

    @NotNull
    private LocalDate plannedEndDate;

    private LocalDate actualStartDate;

    private LocalDate actualReturnDate;

    @Min(0)
    private Integer startMileage;

    @Min(0)
    private Integer endMileage;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(precision = 5, scale = 2)
    private BigDecimal startFuelPercent;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(precision = 5, scale = 2)
    private BigDecimal endFuelPercent;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RentalStatus status = RentalStatus.PENDING;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyRate;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Builder.Default
    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercent = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal deposit;

    @Column(precision = 10, scale = 2)
    private BigDecimal fuelCompensation;

    @Size(max = 1000)
    @Column(length = 1000)
    private String notes;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public long getPlannedDays() {
        if (plannedStartDate == null || plannedEndDate == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(plannedStartDate, plannedEndDate);
    }

    public long getActualDays() {
        if (actualStartDate == null || actualReturnDate == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(actualStartDate, actualReturnDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rental rental = (Rental) o;
        return id != null && id.equals(rental.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
