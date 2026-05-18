import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/rental.dart';
import '../../providers/rental_provider.dart';
import '../../services/rental_service.dart';
import '../../widgets/status_chip.dart';

class RentalDetailScreen extends StatefulWidget {
  final String rentalId;
  const RentalDetailScreen({super.key, required this.rentalId});

  @override
  State<RentalDetailScreen> createState() => _RentalDetailScreenState();
}

class _RentalDetailScreenState extends State<RentalDetailScreen> {
  Rental? _rental;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    try {
      final r = await RentalService().getRentalById(widget.rentalId);
      if (mounted) setState(() { _rental = r; _loading = false; });
    } catch (_) {
      if (mounted) setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_loading) {
      return Scaffold(appBar: AppBar(), body: const Center(child: CircularProgressIndicator()));
    }
    final r = _rental;
    if (r == null) {
      return Scaffold(appBar: AppBar(), body: const Center(child: Text('Не найдено')));
    }
    return Scaffold(
      appBar: AppBar(title: Text(r.carInfo)),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Card(child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Row(children: [
                Expanded(child: Text(r.carInfo, style: Theme.of(context).textTheme.titleLarge)),
                StatusChip(label: r.status.label, color: r.status.color),
              ]),
              const Divider(height: 24),
              _row('Водитель', r.driverName),
              _row('Период', '${r.plannedStartDate} — ${r.plannedEndDate}'),
              _row('Дней', '${r.plannedDays}'),
              _row('Цена/день', '${r.dailyRate} ₽'),
              if (r.discountPercent > 0)
                _row('Скидка', '${r.discountPercent}%'),
              if (r.totalAmount != null)
                _row('Итого', '${r.totalAmount!.toStringAsFixed(0)} ₽'),
              if (r.deposit != null)
                _row('Депозит', '${r.deposit!.toStringAsFixed(0)} ₽'),
            ]),
          )),
          if (r.actualStartDate != null) ...[
            const SizedBox(height: 12),
            Card(child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(children: [
                _row('Фактическая выдача', r.actualStartDate!),
                if (r.startMileage != null)
                  _row('Пробег при выдаче', '${r.startMileage} км'),
                if (r.startFuelPercent != null)
                  _row('Топливо при выдаче', '${r.startFuelPercent}%'),
                if (r.actualReturnDate != null)
                  _row('Дата возврата', r.actualReturnDate!),
                if (r.endMileage != null)
                  _row('Пробег при возврате', '${r.endMileage} км'),
                if (r.endFuelPercent != null)
                  _row('Топливо при возврате', '${r.endFuelPercent}%'),
                if (r.fuelCompensation != null && r.fuelCompensation! > 0)
                  _row('Компенсация топлива', '${r.fuelCompensation!.toStringAsFixed(0)} ₽'),
              ]),
            )),
          ],
          if (r.notes != null) ...[
            const SizedBox(height: 12),
            Card(child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                Text('Примечания', style: Theme.of(context).textTheme.labelLarge),
                const SizedBox(height: 8),
                Text(r.notes!),
              ]),
            )),
          ],
          const SizedBox(height: 24),
          _actions(r),
        ],
      ),
    );
  }

  Widget _actions(Rental r) {
    final p = context.read<RentalProvider>();
    return switch (r.status) {
      RentalStatus.pending => Column(children: [
          FilledButton.icon(
            icon: const Icon(Icons.play_arrow),
            label: const Text('Выдать автомобиль'),
            onPressed: () => _startDialog(r, p),
          ),
          const SizedBox(height: 8),
          OutlinedButton.icon(
            icon: const Icon(Icons.cancel_outlined),
            label: const Text('Отменить бронирование'),
            onPressed: () => _cancel(r, p),
          ),
        ]),
      RentalStatus.active => Column(children: [
          FilledButton.icon(
            icon: const Icon(Icons.check),
            label: const Text('Принять автомобиль'),
            onPressed: () => _completeDialog(r, p),
          ),
          const SizedBox(height: 8),
          OutlinedButton.icon(
            icon: const Icon(Icons.cancel_outlined),
            label: const Text('Отменить'),
            onPressed: () => _cancel(r, p),
          ),
        ]),
      _ => const SizedBox.shrink(),
    };
  }

  void _startDialog(Rental r, RentalProvider p) {
    final mCtrl = TextEditingController(text: '${r.startMileage ?? 0}');
    final fCtrl = TextEditingController(text: '100');
    showDialog(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('Выдача автомобиля'),
        content: Column(mainAxisSize: MainAxisSize.min, children: [
          TextField(controller: mCtrl, decoration: const InputDecoration(labelText: 'Пробег, км'), keyboardType: TextInputType.number),
          const SizedBox(height: 12),
          TextField(controller: fCtrl, decoration: const InputDecoration(labelText: 'Уровень топлива, %'), keyboardType: TextInputType.number),
        ]),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Отмена')),
          FilledButton(
            onPressed: () async {
              await p.startRental(r.id, int.parse(mCtrl.text), double.parse(fCtrl.text));
              if (mounted) { Navigator.pop(context); _load(); }
            },
            child: const Text('Выдать'),
          ),
        ],
      ),
    );
  }

  void _completeDialog(Rental r, RentalProvider p) {
    final mCtrl = TextEditingController();
    final fCtrl = TextEditingController(text: '100');
    showDialog(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('Возврат автомобиля'),
        content: Column(mainAxisSize: MainAxisSize.min, children: [
          TextField(controller: mCtrl, decoration: const InputDecoration(labelText: 'Пробег при возврате, км'), keyboardType: TextInputType.number),
          const SizedBox(height: 12),
          TextField(controller: fCtrl, decoration: const InputDecoration(labelText: 'Уровень топлива, %'), keyboardType: TextInputType.number),
        ]),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Отмена')),
          FilledButton(
            onPressed: () async {
              await p.completeRental(r.id, {
                'endMileage': int.parse(mCtrl.text),
                'endFuelPercent': double.parse(fCtrl.text),
              });
              if (mounted) { Navigator.pop(context); _load(); }
            },
            child: const Text('Принять'),
          ),
        ],
      ),
    );
  }

  void _cancel(Rental r, RentalProvider p) async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('Отменить аренду?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Нет')),
          FilledButton(onPressed: () => Navigator.pop(context, true), child: const Text('Да')),
        ],
      ),
    );
    if (ok == true) {
      await p.cancelRental(r.id);
      if (mounted) _load();
    }
  }

  Widget _row(String label, String value) => Padding(
        padding: const EdgeInsets.symmetric(vertical: 5),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(label, style: const TextStyle(color: Colors.grey)),
            Text(value, style: const TextStyle(fontWeight: FontWeight.w500)),
          ],
        ),
      );
}
