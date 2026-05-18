package ru.superu.rentcarregister.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.superu.rentcarregister.model.Rental;
import ru.superu.rentcarregister.model.enums.RentalStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface RentalRepository extends JpaRepository<Rental, UUID> {

    List<Rental> findByDriverId(UUID driverId);

    List<Rental> findByCarId(UUID carId);

    List<Rental> findByStatus(RentalStatus status);

    List<Rental> findByDriverIdAndStatus(UUID driverId, RentalStatus status);

    @Query("""
            SELECT r FROM Rental r WHERE r.car.id = :carId
            AND r.status IN ('PENDING', 'ACTIVE')
            AND r.plannedStartDate <= :endDate
            AND r.plannedEndDate >= :startDate
            """)
    List<Rental> findConflictingRentals(@Param("carId") UUID carId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT r FROM Rental r WHERE r.status = 'ACTIVE' AND r.plannedEndDate < :today")
    List<Rental> findOverdueRentals(@Param("today") LocalDate today);

    @Query("""
            SELECT COUNT(r) FROM Rental r WHERE r.driver.id = :driverId
            AND r.status = 'COMPLETED'
            """)
    long countCompletedByDriver(@Param("driverId") UUID driverId);

    @Query("""
            SELECT r FROM Rental r WHERE r.status = 'COMPLETED'
            AND r.actualReturnDate >= :from AND r.actualReturnDate <= :to
            """)
    List<Rental> findCompletedInPeriod(@Param("from") LocalDate from,
                                       @Param("to") LocalDate to);
}
