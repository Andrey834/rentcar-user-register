import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../providers/car_provider.dart';
import '../../providers/driver_provider.dart';
import '../../providers/rental_provider.dart';

class CreateRentalScreen extends StatefulWidget {
  const CreateRentalScreen({super.key});

  @override
  State<CreateRentalScreen> createState() => _CreateRentalScreenState();
}

class _CreateRentalScreenState extends State<CreateRentalScreen> {
  final _form = GlobalKey<FormState>();
  String? _carId;
  String? _driverId;
  DateTime _startDate = DateTime.now().add(const Duration(days: 1));
  DateTime _endDate = DateTime.now().add(const Duration(days: 4));
  final _promo = TextEditingController();
  final _notes = TextEditingController();
  bool _saving = false;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<CarProvider>().loadAll();
      context.read<DriverProvider>().loadAll();
    });
  }

  @override
  void dispose() {
    _promo.dispose();
    _notes.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final cars = context.watch<CarProvider>().cars;
    final drivers = context.watch<DriverProvider>().drivers;

    return Scaffold(
      appBar: AppBar(title: const Text('Новое бронирование')),
      body: Form(
        key: _form,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            DropdownButtonFormField<String>(
              value: _carId,
              decoration: const InputDecoration(labelText: 'Автомобиль'),
              hint: const Text('Выберите автомобиль'),
              items: cars
                  .map((c) => DropdownMenuItem(
                        value: c.id,
                        child: Text('${c.displayName} — ${c.licensePlate}'),
                      ))
                  .toList(),
              onChanged: (v) => setState(() => _carId = v),
              validator: (v) => v == null ? 'Выберите автомобиль' : null,
            ),
            const SizedBox(height: 12),
            DropdownButtonFormField<String>(
              value: _driverId,
              decoration: const InputDecoration(labelText: 'Водитель'),
              hint: const Text('Выберите водителя'),
              items: drivers
                  .map((d) => DropdownMenuItem(
                        value: d.personId,
                        child: Text(d.driverName),
                      ))
                  .toList(),
              onChanged: (v) => setState(() => _driverId = v),
              validator: (v) => v == null ? 'Выберите водителя' : null,
            ),
            const SizedBox(height: 16),
            Row(children: [
              Expanded(
                child: _DateField(
                  label: 'Начало аренды',
                  date: _startDate,
                  onPick: (d) => setState(() {
                    _startDate = d;
                    if (!_endDate.isAfter(_startDate)) {
                      _endDate = _startDate.add(const Duration(days: 1));
                    }
                  }),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _DateField(
                  label: 'Конец аренды',
                  date: _endDate,
                  onPick: (d) => setState(() => _endDate = d),
                  firstDate: _startDate.add(const Duration(days: 1)),
                ),
              ),
            ]),
            const SizedBox(height: 12),
            _DaysBadge(start: _startDate, end: _endDate),
            const SizedBox(height: 12),
            TextFormField(
              controller: _promo,
              decoration: const InputDecoration(
                labelText: 'Промокод (необязательно)',
                prefixIcon: Icon(Icons.discount_outlined),
              ),
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _notes,
              decoration: const InputDecoration(labelText: 'Примечания'),
              maxLines: 2,
            ),
            const SizedBox(height: 24),
            FilledButton(
              onPressed: _saving ? null : _submit,
              child: _saving
                  ? const SizedBox(
                      width: 20,
                      height: 20,
                      child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                    )
                  : const Text('Забронировать'),
            ),
          ],
        ),
      ),
    );
  }

  String _fmt(DateTime d) =>
      '${d.year}-${d.month.toString().padLeft(2, '0')}-${d.day.toString().padLeft(2, '0')}';

  Future<void> _submit() async {
    if (!_form.currentState!.validate()) return;
    setState(() => _saving = true);
    final ok = await context.read<RentalProvider>().createRental({
      'driverId': _driverId,
      'carId': _carId,
      'plannedStartDate': _fmt(_startDate),
      'plannedEndDate': _fmt(_endDate),
      if (_promo.text.isNotEmpty) 'promoCode': _promo.text.trim(),
      if (_notes.text.isNotEmpty) 'notes': _notes.text.trim(),
    });
    if (mounted) {
      setState(() => _saving = false);
      if (ok) {
        Navigator.pop(context);
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(context.read<RentalProvider>().error ?? 'Ошибка'),
          ),
        );
      }
    }
  }
}

class _DateField extends StatelessWidget {
  final String label;
  final DateTime date;
  final void Function(DateTime) onPick;
  final DateTime? firstDate;

  const _DateField({
    required this.label,
    required this.date,
    required this.onPick,
    this.firstDate,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: () async {
        final d = await showDatePicker(
          context: context,
          initialDate: date,
          firstDate: firstDate ?? DateTime.now(),
          lastDate: DateTime.now().add(const Duration(days: 365)),
        );
        if (d != null) onPick(d);
      },
      child: InputDecorator(
        decoration: InputDecoration(labelText: label),
        child: Text(
          '${date.day.toString().padLeft(2, '0')}.${date.month.toString().padLeft(2, '0')}.${date.year}',
        ),
      ),
    );
  }
}

class _DaysBadge extends StatelessWidget {
  final DateTime start;
  final DateTime end;
  const _DaysBadge({required this.start, required this.end});

  @override
  Widget build(BuildContext context) {
    final days = end.difference(start).inDays;
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.primaryContainer,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.schedule,
              color: Theme.of(context).colorScheme.onPrimaryContainer),
          const SizedBox(width: 8),
          Text(
            'Продолжительность: $days дн.',
            style: TextStyle(
              color: Theme.of(context).colorScheme.onPrimaryContainer,
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      ),
    );
  }
}
