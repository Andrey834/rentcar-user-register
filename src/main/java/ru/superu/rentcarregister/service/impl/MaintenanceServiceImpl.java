package ru.superu.rentcarregister.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.superu.rentcarregister.dto.MaintenanceRecordDto;
import ru.superu.rentcarregister.exception.CarNotFoundException;
import ru.superu.rentcarregister.model.Car;
import ru.superu.rentcarregister.model.MaintenanceRecord;
import ru.superu.rentcarregister.model.enums.CarStatus;
import ru.superu.rentcarregister.model.enums.MaintenanceType;
import ru.superu.rentcarregister.repository.CarRepository;
import ru.superu.rentcarregister.repository.MaintenanceRecordRepository;
import ru.superu.rentcarregister.service.MaintenanceService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRecordRepository maintenanceRepository;
    private final CarRepository carRepository;

    @Override
    @Transactional
    public MaintenanceRecordDto addRecord(UUID carId, MaintenanceRecordDto dto) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException(carId));

        MaintenanceRecord record = MaintenanceRecord.builder()
                .car(car)
                .date(dto.getDate())
                .mileageAtService(dto.getMileageAtService())
                .type(dto.getType())
                .description(dto.getDescription())
                .cost(dto.getCost())
                .provider(dto.getProvider())
                .nextServiceMileage(dto.getNextServiceMileage())
                .nextServiceDate(dto.getNextServiceDate())
                .build();

        if (dto.getNextServiceMileage() != null) {
            car.setNextMaintenanceMileage(dto.getNextServiceMileage());
        }
        if (dto.getMileageAtService() != null) {
            car.setCurrentMileage(dto.getMileageAtService());
        }
        carRepository.save(car);

        MaintenanceRecord saved = maintenanceRepository.save(record);
        log.info("Maintenance recorded: car={} type={} cost={}",
                car.getLicensePlate(), dto.getType(), dto.getCost());
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceRecordDto> getRecordsByCar(UUID carId) {
        return maintenanceRepository.findByCarIdOrderByDateDesc(carId)
                .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceRecordDto> getRecordsByCarAndType(UUID carId, MaintenanceType type) {
        return maintenanceRepository.findByCarIdAndType(carId, type)
                .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceRecordDto> getRecordsInPeriod(LocalDate from, LocalDate to) {
        return maintenanceRepository.findByDateBetween(from, to)
                .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public void deleteRecord(UUID id) {
        maintenanceRepository.deleteById(id);
        log.info("Maintenance record deleted: {}", id);
    }

    private MaintenanceRecordDto toDto(MaintenanceRecord r) {
        String carInfo = r.getCar().getBrand() + " " + r.getCar().getModel()
                         + " (" + r.getCar().getLicensePlate() + ")";
        return MaintenanceRecordDto.builder()
                .id(r.getId())
                .carId(r.getCar().getId())
                .carInfo(carInfo)
                .date(r.getDate())
                .mileageAtService(r.getMileageAtService())
                .type(r.getType())
                .description(r.getDescription())
                .cost(r.getCost())
                .provider(r.getProvider())
                .nextServiceMileage(r.getNextServiceMileage())
                .nextServiceDate(r.getNextServiceDate())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
