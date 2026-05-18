import '../config/api_config.dart';
import '../models/car.dart';
import 'api_client.dart';

class CarService {
  final _client = ApiClient();

  Future<List<Car>> getAllCars() async {
    final response = await _client.get(ApiConfig.cars);
    return (response.data as List).map((e) => Car.fromJson(e)).toList();
  }

  Future<List<Car>> getAvailableCars(String startDate, String endDate) async {
    final response = await _client.get(
      '${ApiConfig.cars}/available',
      params: {'startDate': startDate, 'endDate': endDate},
    );
    return (response.data as List).map((e) => Car.fromJson(e)).toList();
  }

  Future<List<Car>> getCarsByStatus(String status) async {
    final response = await _client.get(ApiConfig.cars, params: {'status': status});
    return (response.data as List).map((e) => Car.fromJson(e)).toList();
  }

  Future<List<Car>> getCarsNeedingMaintenance() async {
    final response = await _client.get('${ApiConfig.cars}/maintenance-needed');
    return (response.data as List).map((e) => Car.fromJson(e)).toList();
  }

  Future<Car> getCarById(String id) async {
    final response = await _client.get('${ApiConfig.cars}/$id');
    return Car.fromJson(response.data);
  }

  Future<Car> addCar(Map<String, dynamic> data) async {
    final response = await _client.post(ApiConfig.cars, data: data);
    return Car.fromJson(response.data);
  }

  Future<Car> updateCar(String id, Map<String, dynamic> data) async {
    final response = await _client.put('${ApiConfig.cars}/$id', data: data);
    return Car.fromJson(response.data);
  }

  Future<Car> updateStatus(String id, String status) async {
    final response = await _client.patch(
      '${ApiConfig.cars}/$id/status',
      params: {'status': status},
    );
    return Car.fromJson(response.data);
  }

  Future<void> deleteCar(String id) => _client.delete('${ApiConfig.cars}/$id');
}
