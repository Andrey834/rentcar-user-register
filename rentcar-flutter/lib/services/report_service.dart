import '../config/api_config.dart';
import '../models/report.dart';
import 'api_client.dart';

class ReportService {
  final _client = ApiClient();

  Future<FinancialReport> getFinancialReport(String from, String to) async {
    final response = await _client.get(
      '${ApiConfig.reports}/financial',
      params: {'from': from, 'to': to},
    );
    return FinancialReport.fromJson(response.data);
  }

  Future<List<CarSummary>> getAllCarsSummary() async {
    final response = await _client.get('${ApiConfig.reports}/cars/summary');
    return (response.data as List).map((e) => CarSummary.fromJson(e)).toList();
  }

  Future<CarSummary> getCarSummary(String carId) async {
    final response = await _client.get('${ApiConfig.reports}/cars/$carId/summary');
    return CarSummary.fromJson(response.data);
  }
}
