package ru.superu.rentcarregister.service;

import ru.superu.rentcarregister.dto.CompleteRentalDto;
import ru.superu.rentcarregister.dto.NewRentalDto;
import ru.superu.rentcarregister.dto.RentalDto;
import ru.superu.rentcarregister.model.enums.RentalStatus;

import java.util.List;
import java.util.UUID;

public interface RentalService {
    RentalDto createRental(NewRentalDto dto);
    RentalDto startRental(UUID rentalId, Integer startMileage, java.math.BigDecimal startFuelPercent);
    RentalDto completeRental(UUID rentalId, CompleteRentalDto dto);
    RentalDto cancelRental(UUID rentalId);
    RentalDto getRentalById(UUID id);
    List<RentalDto> getRentalsByDriver(UUID driverId);
    List<RentalDto> getRentalsByCar(UUID carId);
    List<RentalDto> getRentalsByStatus(RentalStatus status);
    List<RentalDto> getOverdueRentals();
}
