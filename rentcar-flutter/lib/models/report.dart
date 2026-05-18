class FinancialReport {
  final String periodFrom;
  final String periodTo;
  final int totalRentals;
  final int completedRentals;
  final int cancelledRentals;
  final double totalRevenue;
  final double totalFuelExpenses;
  final double totalMaintenanceCosts;
  final double netProfit;
  final int totalMileageDriven;
  final double averageRentalDays;
  final int totalActiveCars;
  final int totalRentedCars;
  final int totalMaintenanceCars;

  const FinancialReport({
    required this.periodFrom,
    required this.periodTo,
    required this.totalRentals,
    required this.completedRentals,
    required this.cancelledRentals,
    required this.totalRevenue,
    required this.totalFuelExpenses,
    required this.totalMaintenanceCosts,
    required this.netProfit,
    required this.totalMileageDriven,
    required this.averageRentalDays,
    required this.totalActiveCars,
    required this.totalRentedCars,
    required this.totalMaintenanceCars,
  });

  factory FinancialReport.fromJson(Map<String, dynamic> json) =>
      FinancialReport(
        periodFrom: json['periodFrom'] as String,
        periodTo: json['periodTo'] as String,
        totalRentals: (json['totalRentals'] as num).toInt(),
        completedRentals: (json['completedRentals'] as num).toInt(),
        cancelledRentals: (json['cancelledRentals'] as num).toInt(),
        totalRevenue: (json['totalRevenue'] as num).toDouble(),
        totalFuelExpenses: (json['totalFuelExpenses'] as num).toDouble(),
        totalMaintenanceCosts:
            (json['totalMaintenanceCosts'] as num).toDouble(),
        netProfit: (json['netProfit'] as num).toDouble(),
        totalMileageDriven: (json['totalMileageDriven'] as num).toInt(),
        averageRentalDays: (json['averageRentalDays'] as num).toDouble(),
        totalActiveCars: (json['totalActiveCars'] as num).toInt(),
        totalRentedCars: (json['totalRentedCars'] as num).toInt(),
        totalMaintenanceCars: (json['totalMaintenanceCars'] as num).toInt(),
      );
}

class CarSummary {
  final String carId;
  final String carInfo;
  final String licensePlate;
  final int currentMileage;
  final double totalFuelCost;
  final double totalMaintenanceCost;
  final double totalExpenses;
  final double totalRevenue;
  final double netProfit;
  final int totalRentals;

  const CarSummary({
    required this.carId,
    required this.carInfo,
    required this.licensePlate,
    required this.currentMileage,
    required this.totalFuelCost,
    required this.totalMaintenanceCost,
    required this.totalExpenses,
    required this.totalRevenue,
    required this.netProfit,
    required this.totalRentals,
  });

  factory CarSummary.fromJson(Map<String, dynamic> json) => CarSummary(
        carId: json['carId'] as String,
        carInfo: json['carInfo'] as String,
        licensePlate: json['licensePlate'] as String,
        currentMileage: (json['currentMileage'] as num).toInt(),
        totalFuelCost: (json['totalFuelCost'] as num).toDouble(),
        totalMaintenanceCost: (json['totalMaintenanceCost'] as num).toDouble(),
        totalExpenses: (json['totalExpenses'] as num).toDouble(),
        totalRevenue: (json['totalRevenue'] as num).toDouble(),
        netProfit: (json['netProfit'] as num).toDouble(),
        totalRentals: (json['totalRentals'] as num).toInt(),
      );
}
