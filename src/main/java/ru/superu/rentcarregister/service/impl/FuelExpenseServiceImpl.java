package ru.superu.rentcarregister.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.superu.rentcarregister.dto.FuelExpenseDto;
import ru.superu.rentcarregister.exception.CarNotFoundException;
import ru.superu.rentcarregister.model.Car;
import ru.superu.rentcarregister.model.FuelExpense;
import ru.superu.rentcarregister.repository.CarRepository;
import ru.superu.rentcarregister.repository.FuelExpenseRepository;
import ru.superu.rentcarregister.service.FuelExpenseService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuelExpenseServiceImpl implements FuelExpenseService {

    private final FuelExpenseRepository fuelExpenseRepository;
    private final CarRepository carRepository;

    @Override
    @Transactional
    public FuelExpenseDto addExpense(UUID carId, FuelExpenseDto dto) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException(carId));

        FuelExpense expense = FuelExpense.builder()
                .car(car)
                .date(dto.getDate())
                .liters(dto.getLiters())
                .pricePerLiter(dto.getPricePerLiter())
                .mileageAtFill(dto.getMileageAtFill())
                .station(dto.getStation())
                .build();

        FuelExpense saved = fuelExpenseRepository.save(expense);
        log.info("Fuel expense added: car={} liters={} cost={}", car.getLicensePlate(),
                saved.getLiters(), saved.getTotalCost());
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuelExpenseDto> getExpensesByCar(UUID carId) {
        return fuelExpenseRepository.findByCarIdOrderByDateDesc(carId)
                .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuelExpenseDto> getExpensesInPeriod(LocalDate from, LocalDate to) {
        return fuelExpenseRepository.findByDateBetween(from, to)
                .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public void deleteExpense(UUID id) {
        fuelExpenseRepository.deleteById(id);
        log.info("Fuel expense deleted: {}", id);
    }

    private FuelExpenseDto toDto(FuelExpense e) {
        String carInfo = e.getCar().getBrand() + " " + e.getCar().getModel()
                         + " (" + e.getCar().getLicensePlate() + ")";
        return FuelExpenseDto.builder()
                .id(e.getId())
                .carId(e.getCar().getId())
                .carInfo(carInfo)
                .date(e.getDate())
                .liters(e.getLiters())
                .pricePerLiter(e.getPricePerLiter())
                .totalCost(e.getTotalCost())
                .mileageAtFill(e.getMileageAtFill())
                .station(e.getStation())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
