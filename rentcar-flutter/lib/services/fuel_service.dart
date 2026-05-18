import '../config/api_config.dart';
import '../models/fuel_expense.dart';
import 'api_client.dart';

class FuelService {
  final _client = ApiClient();

  Future<List<FuelExpense>> getExpensesByCar(String carId) async {
    final response = await _client.get('${ApiConfig.fuelExpenses}/cars/$carId');
    return (response.data as List).map((e) => FuelExpense.fromJson(e)).toList();
  }

  Future<FuelExpense> addExpense(String carId, Map<String, dynamic> data) async {
    final response = await _client.post('${ApiConfig.fuelExpenses}/cars/$carId', data: data);
    return FuelExpense.fromJson(response.data);
  }

  Future<void> deleteExpense(String id) =>
      _client.delete('${ApiConfig.fuelExpenses}/$id');
}
