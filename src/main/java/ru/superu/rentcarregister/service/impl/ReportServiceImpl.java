package ru.superu.rentcarregister.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.superu.rentcarregister.dto.CarExpenseSummaryDto;
import ru.superu.rentcarregister.dto.ReportDto;
import ru.superu.rentcarregister.model.Car;
import ru.superu.rentcarregister.model.Rental;
import ru.superu.rentcarregister.model.enums.CarStatus;
import ru.superu.rentcarregister.model.enums.RentalStatus;
import ru.superu.rentcarregister.repository.CarRepository;
import ru.superu.rentcarregister.repository.FuelExpenseRepository;
import ru.superu.rentcarregister.repository.MaintenanceRecordRepository;
import ru.superu.rentcarregister.repository.RentalRepository;
import ru.superu.rentcarregister.service.ReportService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final RentalRepository rentalRepository;
    private final FuelExpenseRepository fuelExpenseRepository;
    private final MaintenanceRecordRepository maintenanceRepository;
    private final CarRepository carRepository;

    @Override
    @Transactional(readOnly = true)
    public ReportDto getFinancialReport(LocalDate from, LocalDate to) {
        List<Rental> rentals = rentalRepository.findCompletedInPeriod(from, to);

        long total = rentalRepository.findByStatus(RentalStatus.PENDING).size()
                     + rentalRepository.findByStatus(RentalStatus.ACTIVE).size()
                     + rentals.size()
                     + rentalRepository.findByStatus(RentalStatus.CANCELLED).size();

        BigDecimal totalRevenue = rentals.stream()
                .map(Rental::getTotalAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFuel = fuelExpenseRepository.sumTotalCostInPeriod(from, to);
        BigDecimal totalMaintenance = maintenanceRepository.sumCostInPeriod(from, to);
        BigDecimal netProfit = totalRevenue.subtract(totalFuel).subtract(totalMaintenance);

        long totalMileage = rentals.stream()
                .filter(r -> r.getStartMileage() != null && r.getEndMileage() != null)
                .mapToLong(r -> r.getEndMileage() - r.getStartMileage())
                .sum();

        double avgDays = rentals.stream()
                .mapToLong(Rental::getActualDays)
                .average()
                .orElse(0.0);

        List<Car> allCars = carRepository.findAll();

        return ReportDto.builder()
                .periodFrom(from)
                .periodTo(to)
                .totalRentals(total)
                .completedRentals(rentals.size())
                .cancelledRentals(rentalRepository.findByStatus(RentalStatus.CANCELLED).size())
                .totalRevenue(totalRevenue)
                .totalFuelExpenses(totalFuel)
                .totalMaintenanceCosts(totalMaintenance)
                .netProfit(netProfit)
                .totalMileageDriven(totalMileage)
                .averageRentalDays(avgDays)
                .totalActiveCars((int) allCars.stream().filter(c -> c.getStatus() == CarStatus.AVAILABLE).count())
                .totalRentedCars((int) allCars.stream().filter(c -> c.getStatus() == CarStatus.RENTED).count())
                .totalMaintenanceCars((int) allCars.stream().filter(c -> c.getStatus() == CarStatus.MAINTENANCE).count())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CarExpenseSummaryDto getCarExpenseSummary(UUID carId) {
        Car car = carRepository.findById(carId).orElseThrow();
        BigDecimal fuelCost = fuelExpenseRepository.sumTotalCostByCarId(carId);
        BigDecimal maintenanceCost = maintenanceRepository.sumCostByCarId(carId);
        BigDecimal totalExpenses = fuelCost.add(maintenanceCost);

        List<Rental> completedRentals = rentalRepository.findByCarId(carId).stream()
                .filter(r -> r.getStatus() == RentalStatus.COMPLETED)
                .toList();
        BigDecimal revenue = completedRentals.stream()
                .map(Rental::getTotalAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String carInfo = car.getBrand() + " " + car.getModel();
        return CarExpenseSummaryDto.builder()
                .carId(carId)
                .carInfo(carInfo)
                .licensePlate(car.getLicensePlate())
                .currentMileage(car.getCurrentMileage())
                .totalFuelCost(fuelCost)
                .totalMaintenanceCost(maintenanceCost)
                .totalExpenses(totalExpenses)
                .totalRevenue(revenue)
                .netProfit(revenue.subtract(totalExpenses))
                .totalRentals(completedRentals.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarExpenseSummaryDto> getAllCarsExpenseSummary() {
        return carRepository.findAll().stream()
                .map(car -> getCarExpenseSummary(car.getId()))
                .toList();
    }
}
