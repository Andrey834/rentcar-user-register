import 'package:flutter/material.dart';
import '../models/report.dart';
import '../services/report_service.dart';

class ReportProvider extends ChangeNotifier {
  final _service = ReportService();

  FinancialReport? _report;
  List<CarSummary> _carSummaries = [];
  bool _loading = false;
  String? _error;

  FinancialReport? get report => _report;
  List<CarSummary> get carSummaries => _carSummaries;
  bool get loading => _loading;
  String? get error => _error;

  Future<void> loadFinancialReport(String from, String to) async {
    _setLoading(true);
    try {
      _report = await _service.getFinancialReport(from, to);
      _error = null;
    } catch (e) {
      _error = e.toString();
    } finally {
      _setLoading(false);
    }
  }

  Future<void> loadCarSummaries() async {
    _setLoading(true);
    try {
      _carSummaries = await _service.getAllCarsSummary();
      _error = null;
    } catch (e) {
      _error = e.toString();
    } finally {
      _setLoading(false);
    }
  }

  void _setLoading(bool v) {
    _loading = v;
    notifyListeners();
  }
}
