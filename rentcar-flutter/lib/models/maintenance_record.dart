class MaintenanceRecord {
  final String id;
  final String carId;
  final String carInfo;
  final String date;
  final int? mileageAtService;
  final String type;
  final String? description;
  final double cost;
  final String? provider;
  final int? nextServiceMileage;
  final String? nextServiceDate;
  final String? createdAt;

  const MaintenanceRecord({
    required this.id,
    required this.carId,
    required this.carInfo,
    required this.date,
    this.mileageAtService,
    required this.type,
    this.description,
    required this.cost,
    this.provider,
    this.nextServiceMileage,
    this.nextServiceDate,
    this.createdAt,
  });

  factory MaintenanceRecord.fromJson(Map<String, dynamic> json) =>
      MaintenanceRecord(
        id: json['id'] as String,
        carId: json['carId'] as String,
        carInfo: json['carInfo'] as String,
        date: json['date'] as String,
        mileageAtService: json['mileageAtService'] as int?,
        type: json['type'] as String,
        description: json['description'] as String?,
        cost: (json['cost'] as num).toDouble(),
        provider: json['provider'] as String?,
        nextServiceMileage: json['nextServiceMileage'] as int?,
        nextServiceDate: json['nextServiceDate'] as String?,
        createdAt: json['createdAt'] as String?,
      );

  Map<String, dynamic> toJson() => {
        'date': date,
        'type': type,
        'cost': cost,
        if (mileageAtService != null) 'mileageAtService': mileageAtService,
        if (description != null) 'description': description,
        if (provider != null) 'provider': provider,
        if (nextServiceMileage != null) 'nextServiceMileage': nextServiceMileage,
        if (nextServiceDate != null) 'nextServiceDate': nextServiceDate,
      };

  String get typeLabel => switch (type) {
        'OIL_CHANGE' => 'Замена масла',
        'TIRE_REPLACEMENT' => 'Замена шин',
        'BRAKE_SERVICE' => 'Тормозная система',
        'SCHEDULED_MAINTENANCE' => 'Плановое ТО',
        'BODY_REPAIR' => 'Кузовной ремонт',
        'ENGINE_REPAIR' => 'Ремонт двигателя',
        'ELECTRICAL' => 'Электрика',
        'SUSPENSION' => 'Подвеска',
        'TRANSMISSION_SERVICE' => 'Трансмиссия',
        _ => 'Прочее',
      };
}
