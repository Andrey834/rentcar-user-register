package ru.superu.rentcarregister.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.superu.rentcarregister.dto.CarDto;
import ru.superu.rentcarregister.dto.NewCarDto;
import ru.superu.rentcarregister.exception.CarNotFoundException;
import ru.superu.rentcarregister.model.Car;
import ru.superu.rentcarregister.model.enums.CarCategory;
import ru.superu.rentcarregister.model.enums.CarStatus;
import ru.superu.rentcarregister.model.enums.FuelType;
import ru.superu.rentcarregister.repository.CarRepository;
import ru.superu.rentcarregister.service.impl.CarServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    private Car testCar;
    private UUID carId;

    @BeforeEach
    void setUp() {
        carId = UUID.randomUUID();
        testCar = Car.builder()
                .id(carId)
                .brand("Toyota")
                .model("Camry")
                .year(2022)
                .licensePlate("А123БВ777")
                .category(CarCategory.STANDARD)
                .status(CarStatus.AVAILABLE)
                .currentMileage(15000)
                .fuelType(FuelType.PETROL)
                .fuelConsumptionPer100km(BigDecimal.valueOf(8.5))
                .dailyRate(BigDecimal.valueOf(3000))
                .seats(5)
                .build();
    }

    @Test
    void addCar_shouldSaveAndReturnDto() {
        NewCarDto dto = NewCarDto.builder()
                .brand("Toyota")
                .model("Camry")
                .year(2022)
                .licensePlate("А123БВ777")
                .category(CarCategory.STANDARD)
                .currentMileage(15000)
                .fuelType(FuelType.PETROL)
                .fuelConsumptionPer100km(BigDecimal.valueOf(8.5))
                .dailyRate(BigDecimal.valueOf(3000))
                .build();

        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        CarDto result = carService.addCar(dto);

        assertThat(result).isNotNull();
        assertThat(result.getBrand()).isEqualTo("Toyota");
        assertThat(result.getModel()).isEqualTo("Camry");
        assertThat(result.getStatus()).isEqualTo(CarStatus.AVAILABLE);
        verify(carRepository).save(any(Car.class));
    }

    @Test
    void getCarById_existingId_returnsDto() {
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));

        CarDto result = carService.getCarById(carId);

        assertThat(result.getId()).isEqualTo(carId);
        assertThat(result.getBrand()).isEqualTo("Toyota");
    }

    @Test
    void getCarById_nonExistingId_throwsException() {
        UUID unknownId = UUID.randomUUID();
        when(carRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.getCarById(unknownId))
                .isInstanceOf(CarNotFoundException.class);
    }

    @Test
    void getAllCars_returnsAllCars() {
        when(carRepository.findAll()).thenReturn(List.of(testCar));

        List<CarDto> result = carService.getAllCars();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBrand()).isEqualTo("Toyota");
    }

    @Test
    void getCarsByStatus_returnsFilteredCars() {
        when(carRepository.findByStatus(CarStatus.AVAILABLE)).thenReturn(List.of(testCar));

        List<CarDto> result = carService.getCarsByStatus(CarStatus.AVAILABLE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(CarStatus.AVAILABLE);
    }

    @Test
    void updateCarStatus_changesStatus() {
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenAnswer(inv -> inv.getArgument(0));

        CarDto result = carService.updateCarStatus(carId, CarStatus.MAINTENANCE);

        assertThat(result.getStatus()).isEqualTo(CarStatus.MAINTENANCE);
        verify(carRepository).save(any(Car.class));
    }

    @Test
    void deleteCar_callsDelete() {
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));

        carService.deleteCar(carId);

        verify(carRepository).delete(testCar);
    }

    @Test
    void updateMileage_updatesMileage() {
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenAnswer(inv -> inv.getArgument(0));

        CarDto result = carService.updateMileage(carId, 20000);

        assertThat(result.getCurrentMileage()).isEqualTo(20000);
    }
}
