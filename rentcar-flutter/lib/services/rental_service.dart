import '../config/api_config.dart';
import '../models/rental.dart';
import 'api_client.dart';

class RentalService {
  final _client = ApiClient();

  Future<List<Rental>> getRentalsByStatus(String status) async {
    final response = await _client.get(ApiConfig.rentals, params: {'status': status});
    return (response.data as List).map((e) => Rental.fromJson(e)).toList();
  }

  Future<List<Rental>> getRentalsByDriver(String driverId) async {
    final response = await _client.get(ApiConfig.rentals, params: {'driverId': driverId});
    return (response.data as List).map((e) => Rental.fromJson(e)).toList();
  }

  Future<List<Rental>> getOverdueRentals() async {
    final response = await _client.get('${ApiConfig.rentals}/overdue');
    return (response.data as List).map((e) => Rental.fromJson(e)).toList();
  }

  Future<Rental> getRentalById(String id) async {
    final response = await _client.get('${ApiConfig.rentals}/$id');
    return Rental.fromJson(response.data);
  }

  Future<Rental> createRental(Map<String, dynamic> data) async {
    final response = await _client.post(ApiConfig.rentals, data: data);
    return Rental.fromJson(response.data);
  }

  Future<Rental> startRental(String id, int startMileage, double startFuelPercent) async {
    final response = await _client.patch(
      '${ApiConfig.rentals}/$id/start',
      params: {
        'startMileage': startMileage,
        'startFuelPercent': startFuelPercent,
      },
    );
    return Rental.fromJson(response.data);
  }

  Future<Rental> completeRental(String id, Map<String, dynamic> data) async {
    final response = await _client.patch('${ApiConfig.rentals}/$id/complete', data: data);
    return Rental.fromJson(response.data);
  }

  Future<Rental> cancelRental(String id) async {
    final response = await _client.patch('${ApiConfig.rentals}/$id/cancel');
    return Rental.fromJson(response.data);
  }
}
