import 'package:flutter/material.dart';
import '../models/car.dart';
import '../services/car_service.dart';

class CarProvider extends ChangeNotifier {
  final _service = CarService();

  List<Car> _cars = [];
  Car? _selected;
  bool _loading = false;
  String? _error;

  List<Car> get cars => _cars;
  Car? get selected => _selected;
  bool get loading => _loading;
  String? get error => _error;

  List<Car> get available =>
      _cars.where((c) => c.status == CarStatus.available).toList();

  List<Car> get needsMaintenance =>
      _cars.where((c) => c.needsMaintenance).toList();

  Future<void> loadAll() => _load(() => _service.getAllCars());

  Future<void> loadByStatus(CarStatus status) =>
      _load(() => _service.getCarsByStatus(status.name.toUpperCase()));

  Future<void> loadAvailableForPeriod(String start, String end) =>
      _load(() => _service.getAvailableCars(start, end));

  Future<void> select(String id) async {
    _setLoading(true);
    try {
      _selected = await _service.getCarById(id);
      _error = null;
    } catch (e) {
      _error = e.toString();
    } finally {
      _setLoading(false);
    }
  }

  Future<bool> addCar(Map<String, dynamic> data) async {
    _setLoading(true);
    try {
      final car = await _service.addCar(data);
      _cars = [car, ..._cars];
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

  Future<bool> updateStatus(String id, CarStatus status) async {
    try {
      final updated = await _service.updateStatus(id, status.name.toUpperCase());
      _cars = _cars.map((c) => c.id == id ? updated : c).toList();
      if (_selected?.id == id) _selected = updated;
      _error = null;
      notifyListeners();
      return true;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> deleteCar(String id) async {
    try {
      await _service.deleteCar(id);
      _cars = _cars.where((c) => c.id != id).toList();
      notifyListeners();
      return true;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<void> _load(Future<List<Car>> Function() fetch) async {
    _setLoading(true);
    try {
      _cars = await fetch();
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
