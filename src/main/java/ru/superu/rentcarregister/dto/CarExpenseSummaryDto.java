package ru.superu.rentcarregister.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarExpenseSummaryDto {
    private UUID carId;
    private String carInfo;
    private String licensePlate;
    private Integer currentMileage;
    private BigDecimal totalFuelCost;
    private BigDecimal totalMaintenanceCost;
    private BigDecimal totalExpenses;
    private BigDecimal totalRevenue;
    private BigDecimal netProfit;
    private long totalRentals;
}
