import 'package:flutter/material.dart';
import '../models/rental.dart';
import '../services/rental_service.dart';

class RentalProvider extends ChangeNotifier {
  final _service = RentalService();

  List<Rental> _rentals = [];
  bool _loading = false;
  String? _error;

  List<Rental> get rentals => _rentals;
  bool get loading => _loading;
  String? get error => _error;

  List<Rental> get active =>
      _rentals.where((r) => r.status == RentalStatus.active).toList();

  Future<void> loadByStatus(RentalStatus status) =>
      _load(() => _service.getRentalsByStatus(status.name.toUpperCase()));

  Future<void> loadOverdue() =>
      _load(() => _service.getOverdueRentals());

  Future<bool> createRental(Map<String, dynamic> data) async {
    _setLoading(true);
    try {
      final rental = await _service.createRental(data);
      _rentals = [rental, ..._rentals];
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

  Future<bool> startRental(String id, int mileage, double fuel) async {
    return _update(() => _service.startRental(id, mileage, fuel), id);
  }

  Future<bool> completeRental(String id, Map<String, dynamic> data) async {
    return _update(() => _service.completeRental(id, data), id);
  }

  Future<bool> cancelRental(String id) async {
    return _update(() => _service.cancelRental(id), id);
  }

  Future<bool> _update(Future<Rental> Function() action, String id) async {
    try {
      final updated = await action();
      _rentals = _rentals.map((r) => r.id == id ? updated : r).toList();
      _error = null;
      notifyListeners();
      return true;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<void> _load(Future<List<Rental>> Function() fetch) async {
    _setLoading(true);
    try {
      _rentals = await fetch();
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
