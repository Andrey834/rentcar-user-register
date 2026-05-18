class ApiConfig {
  // static const String baseUrl = 'http://10.0.2.2:8080'; // Android emulator → localhost
  static const String baseUrl = 'http://localhost:8080'; // macOS desktop / iOS simulator / web

  static const Duration connectTimeout = Duration(seconds: 10);
  static const Duration receiveTimeout = Duration(seconds: 15);

  static const String cars = '/api/v1/cars';
  static const String rentals = '/api/v1/rentals';
  static const String drivers = '/api/v1/drivers';
  static const String fuelExpenses = '/api/v1/fuel-expenses';
  static const String maintenance = '/api/v1/maintenance';
  static const String discounts = '/api/v1/discounts';
  static const String reports = '/api/v1/reports';
  static const String users = '/users/register';
}
