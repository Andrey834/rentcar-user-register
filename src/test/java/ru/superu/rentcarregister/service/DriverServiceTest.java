package ru.superu.rentcarregister.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.superu.rentcarregister.dto.DriverLicenseDto;
import ru.superu.rentcarregister.dto.NewDriverLicenseDto;
import ru.superu.rentcarregister.exception.DriverNotFoundException;
import ru.superu.rentcarregister.exception.InvalidLicenseException;
import ru.superu.rentcarregister.model.DriverLicense;
import ru.superu.rentcarregister.model.Person;
import ru.superu.rentcarregister.model.enums.LicenseCategory;
import ru.superu.rentcarregister.repository.DriverLicenseRepository;
import ru.superu.rentcarregister.repository.PersonDao;
import ru.superu.rentcarregister.service.impl.DriverServiceImpl;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverLicenseRepository licenseRepository;
    @Mock
    private PersonDao personDao;

    @InjectMocks
    private DriverServiceImpl driverService;

    private UUID personId;
    private Person person;
    private DriverLicense license;

    @BeforeEach
    void setUp() {
        personId = UUID.randomUUID();
        person = new Person();
        person.setId(personId);
        person.setFirstName("Алексей");
        person.setLastName("Петров");
        person.setEmail("alex@test.com");

        license = DriverLicense.builder()
                .id(UUID.randomUUID())
                .person(person)
                .licenseNumber("77АА654321")
                .issueDate(LocalDate.now().minusYears(2))
                .expireDate(LocalDate.now().plusYears(8))
                .category(LicenseCategory.B)
                .experienceYears(5)
                .build();
    }

    @Test
    void registerLicense_validData_returnsDto() {
        NewDriverLicenseDto dto = NewDriverLicenseDto.builder()
                .personId(personId)
                .licenseNumber("77АА654321")
                .issueDate(LocalDate.now().minusYears(2))
                .expireDate(LocalDate.now().plusYears(8))
                .category(LicenseCategory.B)
                .experienceYears(5)
                .build();

        when(personDao.findById(personId)).thenReturn(Optional.of(person));
        when(licenseRepository.existsByLicenseNumber("77АА654321")).thenReturn(false);
        when(licenseRepository.save(any(DriverLicense.class))).thenReturn(license);

        DriverLicenseDto result = driverService.registerLicense(dto);

        assertThat(result).isNotNull();
        assertThat(result.getLicenseNumber()).isEqualTo("77АА654321");
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void registerLicense_personNotFound_throwsException() {
        NewDriverLicenseDto dto = NewDriverLicenseDto.builder()
                .personId(personId)
                .licenseNumber("77АА654321")
                .issueDate(LocalDate.now().minusYears(2))
                .expireDate(LocalDate.now().plusYears(8))
                .category(LicenseCategory.B)
                .build();

        when(personDao.findById(personId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.registerLicense(dto))
                .isInstanceOf(DriverNotFoundException.class);
    }

    @Test
    void registerLicense_duplicateNumber_throwsException() {
        NewDriverLicenseDto dto = NewDriverLicenseDto.builder()
                .personId(personId)
                .licenseNumber("77АА654321")
                .issueDate(LocalDate.now().minusYears(2))
                .expireDate(LocalDate.now().plusYears(8))
                .category(LicenseCategory.B)
                .build();

        when(personDao.findById(personId)).thenReturn(Optional.of(person));
        when(licenseRepository.existsByLicenseNumber("77АА654321")).thenReturn(true);

        assertThatThrownBy(() -> driverService.registerLicense(dto))
                .isInstanceOf(InvalidLicenseException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    void registerLicense_expiredDate_throwsException() {
        NewDriverLicenseDto dto = NewDriverLicenseDto.builder()
                .personId(personId)
                .licenseNumber("77АА654321")
                .issueDate(LocalDate.now().minusYears(10))
                .expireDate(LocalDate.now().minusDays(1))
                .category(LicenseCategory.B)
                .build();

        when(personDao.findById(personId)).thenReturn(Optional.of(person));
        when(licenseRepository.existsByLicenseNumber("77АА654321")).thenReturn(false);

        assertThatThrownBy(() -> driverService.registerLicense(dto))
                .isInstanceOf(InvalidLicenseException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void getLicenseByPersonId_existingPerson_returnsDto() {
        when(licenseRepository.findByPersonId(personId)).thenReturn(Optional.of(license));

        DriverLicenseDto result = driverService.getLicenseByPersonId(personId);

        assertThat(result.getLicenseNumber()).isEqualTo("77АА654321");
    }

    @Test
    void getLicenseByPersonId_notFound_throwsException() {
        when(licenseRepository.findByPersonId(personId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.getLicenseByPersonId(personId))
                .isInstanceOf(DriverNotFoundException.class);
    }
}
