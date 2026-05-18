package ru.superu.rentcarregister.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDto {
    private LocalDate periodFrom;
    private LocalDate periodTo;
    private long totalRentals;
    private long completedRentals;
    private long cancelledRentals;
    private BigDecimal totalRevenue;
    private BigDecimal totalFuelExpenses;
    private BigDecimal totalMaintenanceCosts;
    private BigDecimal netProfit;
    private long totalMileageDriven;
    private double averageRentalDays;
    private int totalActiveCars;
    private int totalRentedCars;
    private int totalMaintenanceCars;
}
