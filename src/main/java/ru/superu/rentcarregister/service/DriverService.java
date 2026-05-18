package ru.superu.rentcarregister.service;

import ru.superu.rentcarregister.dto.DriverLicenseDto;
import ru.superu.rentcarregister.dto.NewDriverLicenseDto;

import java.util.List;
import java.util.UUID;

public interface DriverService {
    DriverLicenseDto registerLicense(NewDriverLicenseDto dto);
    DriverLicenseDto getLicenseByPersonId(UUID personId);
    DriverLicenseDto getLicenseById(UUID licenseId);
    List<DriverLicenseDto> getAllLicenses();
    DriverLicenseDto updateLicense(UUID licenseId, NewDriverLicenseDto dto);
    void updateRating(UUID personId, int completedRentals);
}
