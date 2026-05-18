package ru.superu.rentcarregister.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.superu.rentcarregister.model.DriverLicense;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverLicenseRepository extends JpaRepository<DriverLicense, UUID> {

    Optional<DriverLicense> findByPersonId(UUID personId);

    Optional<DriverLicense> findByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumber(String licenseNumber);
}
