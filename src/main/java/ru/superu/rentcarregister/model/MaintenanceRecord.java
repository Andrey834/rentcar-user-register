package ru.superu.rentcarregister.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.superu.rentcarregister.model.enums.MaintenanceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "maintenance_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = "car")
public class MaintenanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @NotNull
    private LocalDate date;

    @Min(0)
    private Integer mileageAtService;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MaintenanceType type;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    @Size(max = 200)
    @Column(length = 200)
    private String provider;

    private Integer nextServiceMileage;

    private LocalDate nextServiceDate;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaintenanceRecord that = (MaintenanceRecord) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
