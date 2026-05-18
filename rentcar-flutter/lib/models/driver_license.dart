class DriverLicense {
  final String id;
  final String personId;
  final String driverName;
  final String licenseNumber;
  final String issueDate;
  final String expireDate;
  final String category;
  final int? experienceYears;
  final double rating;
  final int completedRentals;
  final bool valid;
  final String? createdAt;

  const DriverLicense({
    required this.id,
    required this.personId,
    required this.driverName,
    required this.licenseNumber,
    required this.issueDate,
    required this.expireDate,
    required this.category,
    this.experienceYears,
    required this.rating,
    required this.completedRentals,
    required this.valid,
    this.createdAt,
  });

  factory DriverLicense.fromJson(Map<String, dynamic> json) => DriverLicense(
        id: json['id'] as String,
        personId: json['personId'] as String,
        driverName: json['driverName'] as String,
        licenseNumber: json['licenseNumber'] as String,
        issueDate: json['issueDate'] as String,
        expireDate: json['expireDate'] as String,
        category: json['category'] as String,
        experienceYears: json['experienceYears'] as int?,
        rating: (json['rating'] as num).toDouble(),
        completedRentals: json['completedRentals'] as int,
        valid: json['valid'] as bool,
        createdAt: json['createdAt'] as String?,
      );

  Map<String, dynamic> toJson() => {
        'personId': personId,
        'licenseNumber': licenseNumber,
        'issueDate': issueDate,
        'expireDate': expireDate,
        'category': category,
        if (experienceYears != null) 'experienceYears': experienceYears,
      };
}
