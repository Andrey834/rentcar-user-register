package ru.superu.rentcarregister.service;

import ru.superu.rentcarregister.dto.MaintenanceRecordDto;
import ru.superu.rentcarregister.model.enums.MaintenanceType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MaintenanceService {
    MaintenanceRecordDto addRecord(UUID carId, MaintenanceRecordDto dto);
    List<MaintenanceRecordDto> getRecordsByCar(UUID carId);
    List<MaintenanceRecordDto> getRecordsByCarAndType(UUID carId, MaintenanceType type);
    List<MaintenanceRecordDto> getRecordsInPeriod(LocalDate from, LocalDate to);
    void deleteRecord(UUID id);
}
