import '../config/api_config.dart';
import '../models/driver_license.dart';
import 'api_client.dart';

class DriverService {
  final _client = ApiClient();

  Future<List<DriverLicense>> getAllDrivers() async {
    final response = await _client.get(ApiConfig.drivers);
    return (response.data as List).map((e) => DriverLicense.fromJson(e)).toList();
  }

  Future<DriverLicense> getLicenseByPersonId(String personId) async {
    final response = await _client.get('${ApiConfig.drivers}/$personId/license');
    return DriverLicense.fromJson(response.data);
  }

  Future<DriverLicense> registerLicense(Map<String, dynamic> data) async {
    final response = await _client.post('${ApiConfig.drivers}/license', data: data);
    return DriverLicense.fromJson(response.data);
  }
}
