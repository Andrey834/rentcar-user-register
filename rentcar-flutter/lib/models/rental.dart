import 'package:flutter/material.dart';

enum RentalStatus { pending, active, completed, cancelled, overdue }

extension RentalStatusExt on RentalStatus {
  String get label => switch (this) {
        RentalStatus.pending => 'Ожидает',
        RentalStatus.active => 'Активна',
        RentalStatus.completed => 'Завершена',
        RentalStatus.cancelled => 'Отменена',
        RentalStatus.overdue => 'Просрочена',
      };

  Color get color => switch (this) {
        RentalStatus.pending => Colors.orange,
        RentalStatus.active => Colors.blue,
        RentalStatus.completed => Colors.green,
        RentalStatus.cancelled => Colors.grey,
        RentalStatus.overdue => Colors.red,
      };
}

class Rental {
  final String id;
  final String driverId;
  final String driverName;
  final String carId;
  final String carInfo;
  final String plannedStartDate;
  final String plannedEndDate;
  final String? actualStartDate;
  final String? actualReturnDate;
  final int? startMileage;
  final int? endMileage;
  final double? startFuelPercent;
  final double? endFuelPercent;
  final RentalStatus status;
  final double dailyRate;
  final double discountPercent;
  final double? totalAmount;
  final double? deposit;
  final double? fuelCompensation;
  final int plannedDays;
  final String? notes;
  final String? createdAt;

  const Rental({
    required this.id,
    required this.driverId,
    required this.driverName,
    required this.carId,
    required this.carInfo,
    required this.plannedStartDate,
    required this.plannedEndDate,
    this.actualStartDate,
    this.actualReturnDate,
    this.startMileage,
    this.endMileage,
    this.startFuelPercent,
    this.endFuelPercent,
    required this.status,
    required this.dailyRate,
    required this.discountPercent,
    this.totalAmount,
    this.deposit,
    this.fuelCompensation,
    required this.plannedDays,
    this.notes,
    this.createdAt,
  });

  factory Rental.fromJson(Map<String, dynamic> json) => Rental(
        id: json['id'] as String,
        driverId: json['driverId'] as String,
        driverName: json['driverName'] as String,
        carId: json['carId'] as String,
        carInfo: json['carInfo'] as String,
        plannedStartDate: json['plannedStartDate'] as String,
        plannedEndDate: json['plannedEndDate'] as String,
        actualStartDate: json['actualStartDate'] as String?,
        actualReturnDate: json['actualReturnDate'] as String?,
        startMileage: json['startMileage'] as int?,
        endMileage: json['endMileage'] as int?,
        startFuelPercent: (json['startFuelPercent'] as num?)?.toDouble(),
        endFuelPercent: (json['endFuelPercent'] as num?)?.toDouble(),
        status: RentalStatus.values.firstWhere(
          (e) => e.name.toUpperCase() == json['status'],
          orElse: () => RentalStatus.pending,
        ),
        dailyRate: (json['dailyRate'] as num).toDouble(),
        discountPercent: (json['discountPercent'] as num).toDouble(),
        totalAmount: (json['totalAmount'] as num?)?.toDouble(),
        deposit: (json['deposit'] as num?)?.toDouble(),
        fuelCompensation: (json['fuelCompensation'] as num?)?.toDouble(),
        plannedDays: json['plannedDays'] as int,
        notes: json['notes'] as String?,
        createdAt: json['createdAt'] as String?,
      );
}
