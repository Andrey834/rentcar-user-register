import 'package:flutter/material.dart';
import '../models/driver_license.dart';
import '../services/driver_service.dart';

class DriverProvider extends ChangeNotifier {
  final _service = DriverService();

  List<DriverLicense> _drivers = [];
  bool _loading = false;
  String? _error;

  List<DriverLicense> get drivers => _drivers;
  bool get loading => _loading;
  String? get error => _error;

  Future<void> loadAll() async {
    _setLoading(true);
    try {
      _drivers = await _service.getAllDrivers();
      _error = null;
    } catch (e) {
      _error = e.toString();
    } finally {
      _setLoading(false);
    }
  }

  Future<bool> registerLicense(Map<String, dynamic> data) async {
    _setLoading(true);
    try {
      final license = await _service.registerLicense(data);
      _drivers = [license, ..._drivers];
      _error = null;
      notifyListeners();
      return true;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    } finally {
      _loading = false;
    }
  }

  void _setLoading(bool v) {
    _loading = v;
    notifyListeners();
  }
}
