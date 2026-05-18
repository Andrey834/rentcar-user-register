package ru.superu.rentcarregister.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.superu.rentcarregister.dto.CompleteRentalDto;
import ru.superu.rentcarregister.dto.NewRentalDto;
import ru.superu.rentcarregister.dto.RentalDto;
import ru.superu.rentcarregister.exception.CarNotAvailableException;
import ru.superu.rentcarregister.exception.CarNotFoundException;
import ru.superu.rentcarregister.exception.DriverNotFoundException;
import ru.superu.rentcarregister.exception.InvalidLicenseException;
import ru.superu.rentcarregister.exception.RentalNotFoundException;
import ru.superu.rentcarregister.model.Car;
import ru.superu.rentcarregister.model.DriverLicense;
import ru.superu.rentcarregister.model.Person;
import ru.superu.rentcarregister.model.Rental;
import ru.superu.rentcarregister.model.enums.CarStatus;
import ru.superu.rentcarregister.model.enums.RentalStatus;
import ru.superu.rentcarregister.repository.CarRepository;
import ru.superu.rentcarregister.repository.DriverLicenseRepository;
import ru.superu.rentcarregister.repository.PersonDao;
import ru.superu.rentcarregister.repository.RentalRepository;
import ru.superu.rentcarregister.service.DiscountService;
import ru.superu.rentcarregister.service.RentalService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final PersonDao personDao;
    private final DriverLicenseRepository licenseRepository;
    private final DiscountService discountService;

    private static final BigDecimal FUEL_COMPENSATION_RATE = BigDecimal.valueOf(60);
    private static final BigDecimal DEPOSIT_MULTIPLIER = BigDecimal.valueOf(3);

    @Override
    @Transactional
    public RentalDto createRental(NewRentalDto dto) {
        if (!dto.getPlannedEndDate().isAfter(dto.getPlannedStartDate())) {
            throw new IllegalStateException("End date must be after start date");
        }

        Person driver = personDao.findById(dto.getDriverId())
                .orElseThrow(() -> new DriverNotFoundException(dto.getDriverId()));

        DriverLicense license = licenseRepository.findByPersonId(dto.getDriverId())
                .orElseThrow(() -> new InvalidLicenseException("Driver has no registered license"));

        if (!license.isValid()) {
            throw new InvalidLicenseException("Driver license is expired");
        }

        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new CarNotFoundException(dto.getCarId()));

        if (car.getStatus() == CarStatus.RETIRED || car.getStatus() == CarStatus.MAINTENANCE) {
            throw new CarNotAvailableException("Car is not available: " + car.getStatus());
        }

        List<?> conflicts = rentalRepository.findConflictingRentals(
                car.getId(), dto.getPlannedStartDate(), dto.getPlannedEndDate());
        if (!conflicts.isEmpty()) {
            throw new CarNotAvailableException("Car is already booked for selected period");
        }

        long days = java.time.temporal.ChronoUnit.DAYS.between(
                dto.getPlannedStartDate(), dto.getPlannedEndDate());

        BigDecimal discountPercent = BigDecimal.ZERO;
        if (dto.getPromoCode() != null && !dto.getPromoCode().isBlank()) {
            discountPercent = discountService.applyPromoCode(dto.getPromoCode(), driver.getId(), days);
        } else {
            BigDecimal loyaltyDiscount = discountService.calculateLoyaltyDiscount(driver.getId());
            if (days >= 30) {
                BigDecimal longTermDiscount = BigDecimal.valueOf(15);
                discountPercent = loyaltyDiscount.max(longTermDiscount);
            } else {
                discountPercent = loyaltyDiscount;
            }
        }

        BigDecimal multiplier = BigDecimal.ONE.subtract(
                discountPercent.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        BigDecimal totalAmount = car.getDailyRate()
                .multiply(BigDecimal.valueOf(days))
                .multiply(multiplier)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal deposit = car.getDailyRate().multiply(DEPOSIT_MULTIPLIER).setScale(2, RoundingMode.HALF_UP);

        Rental rental = Rental.builder()
                .driver(driver)
                .car(car)
                .plannedStartDate(dto.getPlannedStartDate())
                .plannedEndDate(dto.getPlannedEndDate())
                .dailyRate(car.getDailyRate())
                .discountPercent(discountPercent)
                .totalAmount(totalAmount)
                .deposit(deposit)
                .notes(dto.getNotes())
                .build();

        Rental saved = rentalRepository.save(rental);
        log.info("Rental created: driver={} car={} period={}/{} amount={}",
                driver.getEmail(), car.getLicensePlate(),
                dto.getPlannedStartDate(), dto.getPlannedEndDate(), totalAmount);
        return toDto(saved);
    }

    @Override
    @Transactional
    public RentalDto startRental(UUID rentalId, Integer startMileage, BigDecimal startFuelPercent) {
        Rental rental = findById(rentalId);
        if (rental.getStatus() != RentalStatus.PENDING) {
            throw new IllegalStateException("Rental cannot be started in status: " + rental.getStatus());
        }
        rental.setStatus(RentalStatus.ACTIVE);
        rental.setActualStartDate(LocalDate.now());
        rental.setStartMileage(startMileage);
        rental.setStartFuelPercent(startFuelPercent);
        rental.getCar().setStatus(CarStatus.RENTED);
        carRepository.save(rental.getCar());
        log.info("Rental started: {}", rentalId);
        return toDto(rentalRepository.save(rental));
    }

    @Override
    @Transactional
    public RentalDto completeRental(UUID rentalId, CompleteRentalDto dto) {
        Rental rental = findById(rentalId);
        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new IllegalStateException("Rental cannot be completed in status: " + rental.getStatus());
        }
        if (rental.getStartMileage() != null && dto.getEndMileage() < rental.getStartMileage()) {
            throw new IllegalStateException("End mileage cannot be less than start mileage");
        }

        rental.setStatus(RentalStatus.COMPLETED);
        rental.setActualReturnDate(LocalDate.now());
        rental.setEndMileage(dto.getEndMileage());
        rental.setEndFuelPercent(dto.getEndFuelPercent());

        if (dto.getNotes() != null) {
            rental.setNotes(dto.getNotes());
        }

        BigDecimal fuelCompensation = calculateFuelCompensation(
                rental.getStartFuelPercent(), dto.getEndFuelPercent());
        rental.setFuelCompensation(fuelCompensation);

        Car car = rental.getCar();
        car.setStatus(CarStatus.AVAILABLE);
        if (dto.getEndMileage() != null) {
            car.setCurrentMileage(dto.getEndMileage());
        }
        carRepository.save(car);

        long completedCount = rentalRepository.countCompletedByDriver(rental.getDriver().getId()) + 1;
        log.info("Rental completed: {} totalAmount={} fuelCompensation={}",
                rentalId, rental.getTotalAmount(), fuelCompensation);
        return toDto(rentalRepository.save(rental));
    }

    @Override
    @Transactional
    public RentalDto cancelRental(UUID rentalId) {
        Rental rental = findById(rentalId);
        if (rental.getStatus() == RentalStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed rental");
        }
        if (rental.getStatus() == RentalStatus.ACTIVE) {
            rental.getCar().setStatus(CarStatus.AVAILABLE);
            carRepository.save(rental.getCar());
        }
        rental.setStatus(RentalStatus.CANCELLED);
        log.info("Rental cancelled: {}", rentalId);
        return toDto(rentalRepository.save(rental));
    }

    @Override
    @Transactional(readOnly = true)
    public RentalDto getRentalById(UUID id) {
        return toDto(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalDto> getRentalsByDriver(UUID driverId) {
        return rentalRepository.findByDriverId(driverId).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalDto> getRentalsByCar(UUID carId) {
        return rentalRepository.findByCarId(carId).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalDto> getRentalsByStatus(RentalStatus status) {
        return rentalRepository.findByStatus(status).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalDto> getOverdueRentals() {
        return rentalRepository.findOverdueRentals(LocalDate.now()).stream().map(this::toDto).toList();
    }

    private BigDecimal calculateFuelCompensation(BigDecimal startFuel, BigDecimal endFuel) {
        if (startFuel == null || endFuel == null) return BigDecimal.ZERO;
        BigDecimal diff = startFuel.subtract(endFuel);
        if (diff.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        return diff.multiply(FUEL_COMPENSATION_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    private Rental findById(UUID id) {
        return rentalRepository.findById(id).orElseThrow(() -> new RentalNotFoundException(id));
    }

    private RentalDto toDto(Rental r) {
        String driverName = r.getDriver().getFirstName() + " " + r.getDriver().getLastName();
        String carInfo = r.getCar().getBrand() + " " + r.getCar().getModel() + " (" + r.getCar().getLicensePlate() + ")";
        return RentalDto.builder()
                .id(r.getId())
                .driverId(r.getDriver().getId())
                .driverName(driverName)
                .carId(r.getCar().getId())
                .carInfo(carInfo)
                .plannedStartDate(r.getPlannedStartDate())
                .plannedEndDate(r.getPlannedEndDate())
                .actualStartDate(r.getActualStartDate())
                .actualReturnDate(r.getActualReturnDate())
                .startMileage(r.getStartMileage())
                .endMileage(r.getEndMileage())
                .startFuelPercent(r.getStartFuelPercent())
                .endFuelPercent(r.getEndFuelPercent())
                .status(r.getStatus())
                .dailyRate(r.getDailyRate())
                .discountPercent(r.getDiscountPercent())
                .totalAmount(r.getTotalAmount())
                .deposit(r.getDeposit())
                .fuelCompensation(r.getFuelCompensation())
                .plannedDays(r.getPlannedDays())
                .notes(r.getNotes())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
