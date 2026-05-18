package ru.superu.rentcarregister.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.superu.rentcarregister.model.FuelExpense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface FuelExpenseRepository extends JpaRepository<FuelExpense, UUID> {

    List<FuelExpense> findByCarIdOrderByDateDesc(UUID carId);

    List<FuelExpense> findByDateBetween(LocalDate from, LocalDate to);

    @Query("SELECT COALESCE(SUM(f.totalCost), 0) FROM FuelExpense f WHERE f.car.id = :carId")
    BigDecimal sumTotalCostByCarId(@Param("carId") UUID carId);

    @Query("SELECT COALESCE(SUM(f.totalCost), 0) FROM FuelExpense f WHERE f.date >= :from AND f.date <= :to")
    BigDecimal sumTotalCostInPeriod(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
