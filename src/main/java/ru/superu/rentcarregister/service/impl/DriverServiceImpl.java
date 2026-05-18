package ru.superu.rentcarregister.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.superu.rentcarregister.dto.DriverLicenseDto;
import ru.superu.rentcarregister.dto.NewDriverLicenseDto;
import ru.superu.rentcarregister.exception.DriverNotFoundException;
import ru.superu.rentcarregister.exception.InvalidLicenseException;
import ru.superu.rentcarregister.model.DriverLicense;
import ru.superu.rentcarregister.model.Person;
import ru.superu.rentcarregister.repository.DriverLicenseRepository;
import ru.superu.rentcarregister.repository.PersonDao;
import ru.superu.rentcarregister.service.DriverService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverLicenseRepository licenseRepository;
    private final PersonDao personDao;

    @Override
    @Transactional
    public DriverLicenseDto registerLicense(NewDriverLicenseDto dto) {
        Person person = personDao.findById(dto.getPersonId())
                .orElseThrow(() -> new DriverNotFoundException("Person not found: " + dto.getPersonId()));

        if (licenseRepository.existsByLicenseNumber(dto.getLicenseNumber())) {
            throw new InvalidLicenseException("License number already registered: " + dto.getLicenseNumber());
        }
        if (dto.getExpireDate().isBefore(LocalDate.now())) {
            throw new InvalidLicenseException("License is already expired: " + dto.getExpireDate());
        }

        DriverLicense license = DriverLicense.builder()
                .person(person)
                .licenseNumber(dto.getLicenseNumber())
                .issueDate(dto.getIssueDate())
                .expireDate(dto.getExpireDate())
                .category(dto.getCategory())
                .experienceYears(dto.getExperienceYears())
                .build();

        DriverLicense saved = licenseRepository.save(license);
        log.info("Driver license registered for person: {}", person.getEmail());
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverLicenseDto getLicenseByPersonId(UUID personId) {
        return toDto(licenseRepository.findByPersonId(personId)
                .orElseThrow(() -> new DriverNotFoundException("License not found for person: " + personId)));
    }

    @Override
    @Transactional(readOnly = true)
    public DriverLicenseDto getLicenseById(UUID licenseId) {
        return toDto(licenseRepository.findById(licenseId)
                .orElseThrow(() -> new DriverNotFoundException(licenseId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverLicenseDto> getAllLicenses() {
        return licenseRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public DriverLicenseDto updateLicense(UUID licenseId, NewDriverLicenseDto dto) {
        DriverLicense license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new DriverNotFoundException(licenseId));
        license.setLicenseNumber(dto.getLicenseNumber());
        license.setIssueDate(dto.getIssueDate());
        license.setExpireDate(dto.getExpireDate());
        license.setCategory(dto.getCategory());
        license.setExperienceYears(dto.getExperienceYears());
        return toDto(licenseRepository.save(license));
    }

    @Override
    @Transactional
    public void updateRating(UUID personId, int completedRentals) {
        DriverLicense license = licenseRepository.findByPersonId(personId)
                .orElseThrow(() -> new DriverNotFoundException("License not found for person: " + personId));
        license.setCompletedRentals(completedRentals);
        // базовый расчёт рейтинга: 5.0 за первые 10 аренд, затем по отзывам
        BigDecimal rating = completedRentals >= 10
                ? BigDecimal.valueOf(4.8)
                : BigDecimal.valueOf(5.0);
        license.setRating(rating);
        licenseRepository.save(license);
    }

    private DriverLicenseDto toDto(DriverLicense license) {
        String name = license.getPerson().getFirstName() + " " + license.getPerson().getLastName();
        return DriverLicenseDto.builder()
                .id(license.getId())
                .personId(license.getPerson().getId())
                .driverName(name)
                .licenseNumber(license.getLicenseNumber())
                .issueDate(license.getIssueDate())
                .expireDate(license.getExpireDate())
                .category(license.getCategory())
                .experienceYears(license.getExperienceYears())
                .rating(license.getRating())
                .completedRentals(license.getCompletedRentals())
                .valid(license.isValid())
                .createdAt(license.getCreatedAt())
                .build();
    }
}
