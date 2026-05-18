import 'package:flutter/material.dart';

enum CarStatus { available, rented, maintenance, reserved, retired }
enum CarCategory { economy, standard, comfort, premium, suv, minivan, cargo }
enum FuelType { petrol, diesel, electric, hybrid, gas }
enum Transmission { manual, automatic, robotic, cvt }

extension CarStatusExt on CarStatus {
  String get label => switch (this) {
        CarStatus.available => 'Доступен',
        CarStatus.rented => 'В аренде',
        CarStatus.maintenance => 'ТО',
        CarStatus.reserved => 'Зарезервирован',
        CarStatus.retired => 'Списан',
      };

  Color get color => switch (this) {
        CarStatus.available => Colors.green,
        CarStatus.rented => Colors.blue,
        CarStatus.maintenance => Colors.orange,
        CarStatus.reserved => Colors.purple,
        CarStatus.retired => Colors.grey,
      };
}

extension CarCategoryExt on CarCategory {
  String get label => switch (this) {
        CarCategory.economy => 'Эконом',
        CarCategory.standard => 'Стандарт',
        CarCategory.comfort => 'Комфорт',
        CarCategory.premium => 'Премиум',
        CarCategory.suv => 'Внедорожник',
        CarCategory.minivan => 'Минивэн',
        CarCategory.cargo => 'Грузовой',
      };
}

extension FuelTypeExt on FuelType {
  String get label => switch (this) {
        FuelType.petrol => 'Бензин',
        FuelType.diesel => 'Дизель',
        FuelType.electric => 'Электро',
        FuelType.hybrid => 'Гибрид',
        FuelType.gas => 'Газ',
      };
}

class Car {
  final String id;
  final String brand;
  final String model;
  final int year;
  final String licensePlate;
  final String? vin;
  final CarCategory category;
  final CarStatus status;
  final int currentMileage;
  final FuelType fuelType;
  final double fuelConsumptionPer100km;
  final double dailyRate;
  final String? color;
  final int? seats;
  final Transmission? transmission;
  final String? insuranceExpireDate;
  final int? nextMaintenanceMileage;
  final String? description;
  final String? createdAt;

  const Car({
    required this.id,
    required this.brand,
    required this.model,
    required this.year,
    required this.licensePlate,
    this.vin,
    required this.category,
    required this.status,
    required this.currentMileage,
    required this.fuelType,
    required this.fuelConsumptionPer100km,
    required this.dailyRate,
    this.color,
    this.seats,
    this.transmission,
    this.insuranceExpireDate,
    this.nextMaintenanceMileage,
    this.description,
    this.createdAt,
  });

  String get displayName => '$brand $model ($year)';

  bool get needsMaintenance =>
      nextMaintenanceMileage != null &&
      currentMileage >= nextMaintenanceMileage!;

  factory Car.fromJson(Map<String, dynamic> json) => Car(
        id: json['id'] as String,
        brand: json['brand'] as String,
        model: json['model'] as String,
        year: json['year'] as int,
        licensePlate: json['licensePlate'] as String,
        vin: json['vin'] as String?,
        category: CarCategory.values.firstWhere(
          (e) => e.name.toUpperCase() == json['category'],
          orElse: () => CarCategory.standard,
        ),
        status: CarStatus.values.firstWhere(
          (e) => e.name.toUpperCase() == json['status'],
          orElse: () => CarStatus.available,
        ),
        currentMileage: json['currentMileage'] as int,
        fuelType: FuelType.values.firstWhere(
          (e) => e.name.toUpperCase() == json['fuelType'],
          orElse: () => FuelType.petrol,
        ),
        fuelConsumptionPer100km:
            (json['fuelConsumptionPer100km'] as num).toDouble(),
        dailyRate: (json['dailyRate'] as num).toDouble(),
        color: json['color'] as String?,
        seats: json['seats'] as int?,
        transmission: json['transmission'] != null
            ? Transmission.values.firstWhere(
                (e) => e.name.toUpperCase() == json['transmission'],
                orElse: () => Transmission.automatic,
              )
            : null,
        insuranceExpireDate: json['insuranceExpireDate'] as String?,
        nextMaintenanceMileage: json['nextMaintenanceMileage'] as int?,
        description: json['description'] as String?,
        createdAt: json['createdAt'] as String?,
      );

  Map<String, dynamic> toJson() => {
        'brand': brand,
        'model': model,
        'year': year,
        'licensePlate': licensePlate,
        if (vin != null) 'vin': vin,
        'category': category.name.toUpperCase(),
        'currentMileage': currentMileage,
        'fuelType': fuelType.name.toUpperCase(),
        'fuelConsumptionPer100km': fuelConsumptionPer100km,
        'dailyRate': dailyRate,
        if (color != null) 'color': color,
        if (seats != null) 'seats': seats,
        if (transmission != null)
          'transmission': transmission!.name.toUpperCase(),
        if (insuranceExpireDate != null)
          'insuranceExpireDate': insuranceExpireDate,
        if (nextMaintenanceMileage != null)
          'nextMaintenanceMileage': nextMaintenanceMileage,
        if (description != null) 'description': description,
      };
}
