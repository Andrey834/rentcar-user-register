import '../config/api_config.dart';
import '../models/maintenance_record.dart';
import 'api_client.dart';

class MaintenanceService {
  final _client = ApiClient();

  Future<List<MaintenanceRecord>> getRecordsByCar(String carId) async {
    final response = await _client.get('${ApiConfig.maintenance}/cars/$carId');
    return (response.data as List).map((e) => MaintenanceRecord.fromJson(e)).toList();
  }

  Future<MaintenanceRecord> addRecord(String carId, Map<String, dynamic> data) async {
    final response = await _client.post('${ApiConfig.maintenance}/cars/$carId', data: data);
    return MaintenanceRecord.fromJson(response.data);
  }

  Future<void> deleteRecord(String id) =>
      _client.delete('${ApiConfig.maintenance}/$id');
}
