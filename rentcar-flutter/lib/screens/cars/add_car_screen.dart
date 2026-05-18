import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/car.dart';
import '../../providers/car_provider.dart';

class AddCarScreen extends StatefulWidget {
  const AddCarScreen({super.key});

  @override
  State<AddCarScreen> createState() => _AddCarScreenState();
}

class _AddCarScreenState extends State<AddCarScreen> {
  final _form = GlobalKey<FormState>();
  final _brand = TextEditingController();
  final _model = TextEditingController();
  final _plate = TextEditingController();
  final _vin = TextEditingController();
  final _mileage = TextEditingController(text: '0');
  final _rate = TextEditingController();
  final _fuel = TextEditingController(text: '8.5');
  final _color = TextEditingController();
  final _seats = TextEditingController(text: '5');
  final _desc = TextEditingController();

  int _year = DateTime.now().year;
  CarCategory _category = CarCategory.standard;
  FuelType _fuelType = FuelType.petrol;
  bool _saving = false;

  @override
  void dispose() {
    for (final c in [_brand, _model, _plate, _vin, _mileage, _rate, _fuel, _color, _seats, _desc]) {
      c.dispose();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Добавить автомобиль')),
      body: Form(
        key: _form,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            _field(_brand, 'Марка', required: true),
            _field(_model, 'Модель', required: true),
            Row(children: [
              Expanded(
                child: DropdownButtonFormField<int>(
                  value: _year,
                  decoration: const InputDecoration(labelText: 'Год'),
                  items: List.generate(
                    30,
                    (i) => DropdownMenuItem(
                      value: DateTime.now().year - i,
                      child: Text('${DateTime.now().year - i}'),
                    ),
                  ),
                  onChanged: (v) => setState(() => _year = v!),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: DropdownButtonFormField<CarCategory>(
                  value: _category,
                  decoration: const InputDecoration(labelText: 'Категория'),
                  items: CarCategory.values
                      .map((c) => DropdownMenuItem(value: c, child: Text(c.label)))
                      .toList(),
                  onChanged: (v) => setState(() => _category = v!),
                ),
              ),
            ]),
            const SizedBox(height: 12),
            _field(_plate, 'Гос. номер', required: true),
            _field(_vin, 'VIN'),
            Row(children: [
              Expanded(
                child: DropdownButtonFormField<FuelType>(
                  value: _fuelType,
                  decoration: const InputDecoration(labelText: 'Топливо'),
                  items: FuelType.values
                      .map((f) => DropdownMenuItem(value: f, child: Text(f.label)))
                      .toList(),
                  onChanged: (v) => setState(() => _fuelType = v!),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _field(_fuel, 'Расход л/100км', keyboard: TextInputType.number, required: true),
              ),
            ]),
            const SizedBox(height: 12),
            Row(children: [
              Expanded(child: _field(_mileage, 'Пробег км', keyboard: TextInputType.number)),
              const SizedBox(width: 12),
              Expanded(child: _field(_rate, 'Цена ₽/день', keyboard: TextInputType.number, required: true)),
            ]),
            const SizedBox(height: 12),
            Row(children: [
              Expanded(child: _field(_color, 'Цвет')),
              const SizedBox(width: 12),
              Expanded(child: _field(_seats, 'Мест', keyboard: TextInputType.number)),
            ]),
            const SizedBox(height: 12),
            _field(_desc, 'Описание', maxLines: 2),
            const SizedBox(height: 24),
            FilledButton(
              onPressed: _saving ? null : _submit,
              child: _saving
                  ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2))
                  : const Text('Сохранить'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _field(
    TextEditingController ctrl,
    String label, {
    bool required = false,
    TextInputType keyboard = TextInputType.text,
    int maxLines = 1,
  }) =>
      Padding(
        padding: const EdgeInsets.only(bottom: 12),
        child: TextFormField(
          controller: ctrl,
          decoration: InputDecoration(labelText: label),
          keyboardType: keyboard,
          maxLines: maxLines,
          validator: required
              ? (v) => (v == null || v.isEmpty) ? 'Обязательное поле' : null
              : null,
        ),
      );

  Future<void> _submit() async {
    if (!_form.currentState!.validate()) return;
    setState(() => _saving = true);
    final ok = await context.read<CarProvider>().addCar({
      'brand': _brand.text.trim(),
      'model': _model.text.trim(),
      'year': _year,
      'licensePlate': _plate.text.trim(),
      if (_vin.text.isNotEmpty) 'vin': _vin.text.trim(),
      'category': _category.name.toUpperCase(),
      'currentMileage': int.parse(_mileage.text),
      'fuelType': _fuelType.name.toUpperCase(),
      'fuelConsumptionPer100km': double.parse(_fuel.text),
      'dailyRate': double.parse(_rate.text),
      if (_color.text.isNotEmpty) 'color': _color.text.trim(),
      if (_seats.text.isNotEmpty) 'seats': int.parse(_seats.text),
      if (_desc.text.isNotEmpty) 'description': _desc.text.trim(),
    });
    if (mounted) {
      setState(() => _saving = false);
      if (ok) {
        Navigator.pop(context);
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(context.read<CarProvider>().error ?? 'Ошибка')),
        );
      }
    }
  }
}
