package ru.superu.rentcarregister.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.superu.rentcarregister.model.enums.CarCategory;
import ru.superu.rentcarregister.model.enums.CarStatus;
import ru.superu.rentcarregister.model.enums.FuelType;
import ru.superu.rentcarregister.model.enums.Transmission;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 50)
    @Column(length = 50, nullable = false)
    private String brand;

    @NotBlank
    @Size(max = 50)
    @Column(length = 50, nullable = false)
    private String model;

    @NotNull
    @Min(1900)
    private Integer year;

    @NotBlank
    @Size(max = 20)
    @Column(length = 20, unique = true, nullable = false)
    private String licensePlate;

    @Size(max = 17)
    @Column(length = 17, unique = true)
    private String vin;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CarCategory category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CarStatus status = CarStatus.AVAILABLE;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false)
    private Integer currentMileage;

    @NotNull
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @NotNull
    @DecimalMin("0.1")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal fuelConsumptionPer100km;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyRate;

    @Size(max = 30)
    @Column(length = 30)
    private String color;

    @Min(1)
    @Max(50)
    private Integer seats;

    @Enumerated(EnumType.STRING)
    private Transmission transmission;

    private LocalDate insuranceExpireDate;

    private Integer nextMaintenanceMileage;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return id != null && id.equals(car.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
