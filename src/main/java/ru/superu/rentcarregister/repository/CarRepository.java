package ru.superu.rentcarregister.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.superu.rentcarregister.model.Car;
import ru.superu.rentcarregister.model.enums.CarCategory;
import ru.superu.rentcarregister.model.enums.CarStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {

    List<Car> findByStatus(CarStatus status);

    List<Car> findByCategory(CarCategory category);

    List<Car> findByStatusAndCategory(CarStatus status, CarCategory category);

    Optional<Car> findByLicensePlate(String licensePlate);

    Optional<Car> findByVin(String vin);

    @Query("""
            SELECT c FROM Car c WHERE c.status = 'AVAILABLE'
            AND c.id NOT IN (
                SELECT r.car.id FROM Rental r
                WHERE r.status IN ('PENDING', 'ACTIVE')
                AND r.plannedStartDate <= :endDate
                AND r.plannedEndDate >= :startDate
            )
            """)
    List<Car> findAvailableForPeriod(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    @Query("SELECT c FROM Car c WHERE c.nextMaintenanceMileage IS NOT NULL AND c.currentMileage >= c.nextMaintenanceMileage")
    List<Car> findCarsNeedingMaintenance();
}
