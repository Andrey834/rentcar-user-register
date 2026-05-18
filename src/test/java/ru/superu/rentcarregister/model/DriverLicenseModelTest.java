package ru.superu.rentcarregister.model;

import org.junit.jupiter.api.Test;
import ru.superu.rentcarregister.model.enums.LicenseCategory;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class DriverLicenseModelTest {

    @Test
    void isValid_futureExpireDate_returnsTrue() {
        DriverLicense license = DriverLicense.builder()
                .expireDate(LocalDate.now().plusYears(5))
                .category(LicenseCategory.B)
                .build();

        assertThat(license.isValid()).isTrue();
    }

    @Test
    void isValid_pastExpireDate_returnsFalse() {
        DriverLicense license = DriverLicense.builder()
                .expireDate(LocalDate.now().minusDays(1))
                .category(LicenseCategory.B)
                .build();

        assertThat(license.isValid()).isFalse();
    }

    @Test
    void isValid_todayExpireDate_returnsFalse() {
        DriverLicense license = DriverLicense.builder()
                .expireDate(LocalDate.now())
                .category(LicenseCategory.B)
                .build();

        assertThat(license.isValid()).isFalse();
    }
}
