class FuelExpense {
  final String id;
  final String carId;
  final String carInfo;
  final String date;
  final double liters;
  final double pricePerLiter;
  final double? totalCost;
  final int? mileageAtFill;
  final String? station;
  final String? createdAt;

  const FuelExpense({
    required this.id,
    required this.carId,
    required this.carInfo,
    required this.date,
    required this.liters,
    required this.pricePerLiter,
    this.totalCost,
    this.mileageAtFill,
    this.station,
    this.createdAt,
  });

  factory FuelExpense.fromJson(Map<String, dynamic> json) => FuelExpense(
        id: json['id'] as String,
        carId: json['carId'] as String,
        carInfo: json['carInfo'] as String,
        date: json['date'] as String,
        liters: (json['liters'] as num).toDouble(),
        pricePerLiter: (json['pricePerLiter'] as num).toDouble(),
        totalCost: (json['totalCost'] as num?)?.toDouble(),
        mileageAtFill: json['mileageAtFill'] as int?,
        station: json['station'] as String?,
        createdAt: json['createdAt'] as String?,
      );

  Map<String, dynamic> toJson() => {
        'date': date,
        'liters': liters,
        'pricePerLiter': pricePerLiter,
        if (mileageAtFill != null) 'mileageAtFill': mileageAtFill,
        if (station != null) 'station': station,
      };
}
