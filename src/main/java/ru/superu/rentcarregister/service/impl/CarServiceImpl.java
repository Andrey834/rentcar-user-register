package ru.superu.rentcarregister.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.superu.rentcarregister.dto.CarDto;
import ru.superu.rentcarregister.dto.NewCarDto;
import ru.superu.rentcarregister.exception.CarNotFoundException;
import ru.superu.rentcarregister.model.Car;
import ru.superu.rentcarregister.model.enums.CarCategory;
import ru.superu.rentcarregister.model.enums.CarStatus;
import ru.superu.rentcarregister.repository.CarRepository;
import ru.superu.rentcarregister.service.CarService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Override
    @Transactional
    public CarDto addCar(NewCarDto dto) {
        Car car = Car.builder()
                .brand(dto.getBrand())
                .model(dto.getModel())
                .year(dto.getYear())
                .licensePlate(dto.getLicensePlate().toUpperCase())
                .vin(dto.getVin())
                .category(dto.getCategory())
                .currentMileage(dto.getCurrentMileage())
                .fuelType(dto.getFuelType())
                .fuelConsumptionPer100km(dto.getFuelConsumptionPer100km())
                .dailyRate(dto.getDailyRate())
                .color(dto.getColor())
                .seats(dto.getSeats())
                .transmission(dto.getTransmission())
                .insuranceExpireDate(dto.getInsuranceExpireDate())
                .nextMaintenanceMileage(dto.getNextMaintenanceMileage())
                .description(dto.getDescription())
                .build();
        Car saved = carRepository.save(car);
        log.info("Car added: {} {} ({})", saved.getBrand(), saved.getModel(), saved.getLicensePlate());
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CarDto getCarById(UUID id) {
        return toDto(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarDto> getAllCars() {
        return carRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarDto> getCarsByStatus(CarStatus status) {
        return carRepository.findByStatus(status).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarDto> getCarsByCategory(CarCategory category) {
        return carRepository.findByCategory(category).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarDto> getAvailableCarsForPeriod(LocalDate startDate, LocalDate endDate) {
        return carRepository.findAvailableForPeriod(startDate, endDate).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarDto> getCarsNeedingMaintenance() {
        return carRepository.findCarsNeedingMaintenance().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public CarDto updateCar(UUID id, NewCarDto dto) {
        Car car = findById(id);
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setYear(dto.getYear());
        car.setLicensePlate(dto.getLicensePlate().toUpperCase());
        car.setVin(dto.getVin());
        car.setCategory(dto.getCategory());
        car.setCurrentMileage(dto.getCurrentMileage());
        car.setFuelType(dto.getFuelType());
        car.setFuelConsumptionPer100km(dto.getFuelConsumptionPer100km());
        car.setDailyRate(dto.getDailyRate());
        car.setColor(dto.getColor());
        car.setSeats(dto.getSeats());
        car.setTransmission(dto.getTransmission());
        car.setInsuranceExpireDate(dto.getInsuranceExpireDate());
        car.setNextMaintenanceMileage(dto.getNextMaintenanceMileage());
        car.setDescription(dto.getDescription());
        return toDto(carRepository.save(car));
    }

    @Override
    @Transactional
    public CarDto updateCarStatus(UUID id, CarStatus status) {
        Car car = findById(id);
        car.setStatus(status);
        log.info("Car {} status changed to {}", car.getLicensePlate(), status);
        return toDto(carRepository.save(car));
    }

    @Override
    @Transactional
    public CarDto updateMileage(UUID id, Integer mileage) {
        Car car = findById(id);
        car.setCurrentMileage(mileage);
        return toDto(carRepository.save(car));
    }

    @Override
    @Transactional
    public void deleteCar(UUID id) {
        Car car = findById(id);
        carRepository.delete(car);
        log.info("Car deleted: {}", id);
    }

    private Car findById(UUID id) {
        return carRepository.findById(id).orElseThrow(() -> new CarNotFoundException(id));
    }

    private CarDto toDto(Car car) {
        return CarDto.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .model(car.getModel())
                .year(car.getYear())
                .licensePlate(car.getLicensePlate())
                .vin(car.getVin())
                .category(car.getCategory())
                .status(car.getStatus())
                .currentMileage(car.getCurrentMileage())
                .fuelType(car.getFuelType())
                .fuelConsumptionPer100km(car.getFuelConsumptionPer100km())
                .dailyRate(car.getDailyRate())
                .color(car.getColor())
                .seats(car.getSeats())
                .transmission(car.getTransmission())
                .insuranceExpireDate(car.getInsuranceExpireDate())
                .nextMaintenanceMileage(car.getNextMaintenanceMileage())
                .description(car.getDescription())
                .createdAt(car.getCreatedAt())
                .build();
    }
}
