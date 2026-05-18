package ru.superu.rentcarregister.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fuel_expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = "car")
public class FuelExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @NotNull
    private LocalDate date;

    @NotNull
    @DecimalMin("0.1")
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal liters;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal pricePerLiter;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Min(0)
    private Integer mileageAtFill;

    @Size(max = 100)
    @Column(length = 100)
    private String station;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (liters != null && pricePerLiter != null) {
            this.totalCost = liters.multiply(pricePerLiter);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FuelExpense that = (FuelExpense) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
