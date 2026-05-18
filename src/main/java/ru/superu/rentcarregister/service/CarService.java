package ru.superu.rentcarregister.service;

import ru.superu.rentcarregister.dto.CarDto;
import ru.superu.rentcarregister.dto.NewCarDto;
import ru.superu.rentcarregister.model.enums.CarCategory;
import ru.superu.rentcarregister.model.enums.CarStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CarService {
    CarDto addCar(NewCarDto newCarDto);
    CarDto getCarById(UUID id);
    List<CarDto> getAllCars();
    List<CarDto> getCarsByStatus(CarStatus status);
    List<CarDto> getCarsByCategory(CarCategory category);
    List<CarDto> getAvailableCarsForPeriod(LocalDate startDate, LocalDate endDate);
    List<CarDto> getCarsNeedingMaintenance();
    CarDto updateCar(UUID id, NewCarDto newCarDto);
    CarDto updateCarStatus(UUID id, CarStatus status);
    CarDto updateMileage(UUID id, Integer mileage);
    void deleteCar(UUID id);
}
