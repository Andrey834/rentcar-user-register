package ru.superu.rentcarregister.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.superu.rentcarregister.dto.CompleteRentalDto;
import ru.superu.rentcarregister.dto.NewRentalDto;
import ru.superu.rentcarregister.dto.RentalDto;
import ru.superu.rentcarregister.exception.CarNotAvailableException;
import ru.superu.rentcarregister.exception.InvalidLicenseException;
import ru.superu.rentcarregister.model.*;
import ru.superu.rentcarregister.model.enums.*;
import ru.superu.rentcarregister.repository.*;
import ru.superu.rentcarregister.service.impl.RentalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private PersonDao personDao;
    @Mock
    private DriverLicenseRepository licenseRepository;
    @Mock
    private DiscountService discountService;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private UUID driverId;
    private UUID carId;
    private UUID rentalId;
    private Person driver;
    private Car car;
    private DriverLicense license;
    private Rental rental;

    @BeforeEach
    void setUp() {
        driverId = UUID.randomUUID();
        carId = UUID.randomUUID();
        rentalId = UUID.randomUUID();

        driver = new Person();
        driver.setId(driverId);
        driver.setFirstName("Иван");
        driver.setLastName("Иванов");
        driver.setEmail("ivan@test.com");

        car = Car.builder()
                .id(carId)
                .brand("Toyota")
                .model("Camry")
                .licensePlate("А001АА77")
                .status(CarStatus.AVAILABLE)
                .dailyRate(BigDecimal.valueOf(3000))
                .build();

        license = DriverLicense.builder()
                .id(UUID.randomUUID())
                .person(driver)
                .licenseNumber("77АА123456")
                .issueDate(LocalDate.now().minusYears(3))
                .expireDate(LocalDate.now().plusYears(7))
                .category(LicenseCategory.B)
                .build();

        rental = Rental.builder()
                .id(rentalId)
                .driver(driver)
                .car(car)
                .plannedStartDate(LocalDate.now().plusDays(1))
                .plannedEndDate(LocalDate.now().plusDays(4))
                .dailyRate(BigDecimal.valueOf(3000))
                .discountPercent(BigDecimal.ZERO)
                .totalAmount(BigDecimal.valueOf(9000))
                .deposit(BigDecimal.valueOf(9000))
                .status(RentalStatus.PENDING)
                .build();
    }

    @Test
    void createRental_validData_returnsDto() {
        NewRentalDto dto = NewRentalDto.builder()
                .driverId(driverId)
                .carId(carId)
                .plannedStartDate(LocalDate.now().plusDays(1))
                .plannedEndDate(LocalDate.now().plusDays(4))
                .build();

        when(personDao.findById(driverId)).thenReturn(Optional.of(driver));
        when(licenseRepository.findByPersonId(driverId)).thenReturn(Optional.of(license));
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(rentalRepository.findConflictingRentals(any(), any(), any())).thenReturn(List.of());
        when(discountService.calculateLoyaltyDiscount(driverId)).thenReturn(BigDecimal.ZERO);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        RentalDto result = rentalService.createRental(dto);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(RentalStatus.PENDING);
        verify(rentalRepository).save(any(Rental.class));
    }

    @Test
    void createRental_expiredLicense_throwsException() {
        license.setExpireDate(LocalDate.now().minusDays(1));

        NewRentalDto dto = NewRentalDto.builder()
                .driverId(driverId)
                .carId(carId)
                .plannedStartDate(LocalDate.now().plusDays(1))
                .plannedEndDate(LocalDate.now().plusDays(4))
                .build();

        when(personDao.findById(driverId)).thenReturn(Optional.of(driver));
        when(licenseRepository.findByPersonId(driverId)).thenReturn(Optional.of(license));

        assertThatThrownBy(() -> rentalService.createRental(dto))
                .isInstanceOf(InvalidLicenseException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void createRental_carAlreadyBooked_throwsException() {
        NewRentalDto dto = NewRentalDto.builder()
                .driverId(driverId)
                .carId(carId)
                .plannedStartDate(LocalDate.now().plusDays(1))
                .plannedEndDate(LocalDate.now().plusDays(4))
                .build();

        when(personDao.findById(driverId)).thenReturn(Optional.of(driver));
        when(licenseRepository.findByPersonId(driverId)).thenReturn(Optional.of(license));
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(rentalRepository.findConflictingRentals(any(), any(), any())).thenReturn(List.of(rental));

        assertThatThrownBy(() -> rentalService.createRental(dto))
                .isInstanceOf(CarNotAvailableException.class);
    }

    @Test
    void createRental_endDateBeforeStart_throwsException() {
        NewRentalDto dto = NewRentalDto.builder()
                .driverId(driverId)
                .carId(carId)
                .plannedStartDate(LocalDate.now().plusDays(5))
                .plannedEndDate(LocalDate.now().plusDays(2))
                .build();

        assertThatThrownBy(() -> rentalService.createRental(dto))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void startRental_pendingRental_startsSuccessfully() {
        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(carRepository.save(any())).thenReturn(car);
        when(rentalRepository.save(any(Rental.class))).thenAnswer(inv -> inv.getArgument(0));

        RentalDto result = rentalService.startRental(rentalId, 15000, BigDecimal.valueOf(80));

        assertThat(result.getStatus()).isEqualTo(RentalStatus.ACTIVE);
        assertThat(result.getStartMileage()).isEqualTo(15000);
    }

    @Test
    void completeRental_activeRental_completesSuccessfully() {
        rental.setStatus(RentalStatus.ACTIVE);
        rental.setStartMileage(15000);
        rental.setStartFuelPercent(BigDecimal.valueOf(80));

        CompleteRentalDto dto = CompleteRentalDto.builder()
                .endMileage(15500)
                .endFuelPercent(BigDecimal.valueOf(60))
                .build();

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(carRepository.save(any())).thenReturn(car);
        when(rentalRepository.countCompletedByDriver(driverId)).thenReturn(1L);
        when(rentalRepository.save(any(Rental.class))).thenAnswer(inv -> inv.getArgument(0));

        RentalDto result = rentalService.completeRental(rentalId, dto);

        assertThat(result.getStatus()).isEqualTo(RentalStatus.COMPLETED);
        assertThat(result.getEndMileage()).isEqualTo(15500);
    }

    @Test
    void cancelRental_pendingRental_cancelledSuccessfully() {
        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(inv -> inv.getArgument(0));

        RentalDto result = rentalService.cancelRental(rentalId);

        assertThat(result.getStatus()).isEqualTo(RentalStatus.CANCELLED);
    }

    @Test
    void cancelRental_completedRental_throwsException() {
        rental.setStatus(RentalStatus.COMPLETED);
        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));

        assertThatThrownBy(() -> rentalService.cancelRental(rentalId))
                .isInstanceOf(IllegalStateException.class);
    }
}
