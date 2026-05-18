package ru.superu.rentcarregister.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.superu.rentcarregister.model.MaintenanceRecord;
import ru.superu.rentcarregister.model.enums.MaintenanceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, UUID> {

    List<MaintenanceRecord> findByCarIdOrderByDateDesc(UUID carId);

    List<MaintenanceRecord> findByCarIdAndType(UUID carId, MaintenanceType type);

    List<MaintenanceRecord> findByDateBetween(LocalDate from, LocalDate to);

    @Query("SELECT COALESCE(SUM(m.cost), 0) FROM MaintenanceRecord m WHERE m.car.id = :carId")
    BigDecimal sumCostByCarId(@Param("carId") UUID carId);

    @Query("SELECT COALESCE(SUM(m.cost), 0) FROM MaintenanceRecord m WHERE m.date >= :from AND m.date <= :to")
    BigDecimal sumCostInPeriod(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
