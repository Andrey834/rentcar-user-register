package ru.superu.rentcarregister.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.superu.rentcarregister.model.Discount;
import ru.superu.rentcarregister.model.enums.DiscountType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, UUID> {

    Optional<Discount> findByPromoCode(String promoCode);

    List<Discount> findByActiveTrue();

    List<Discount> findByType(DiscountType type);

    @Query("""
            SELECT d FROM Discount d WHERE d.active = true
            AND (d.validFrom IS NULL OR d.validFrom <= :date)
            AND (d.validTo IS NULL OR d.validTo >= :date)
            AND d.type = :type
            """)
    List<Discount> findActiveByTypeAndDate(@Param("type") DiscountType type,
                                           @Param("date") LocalDate date);
}
