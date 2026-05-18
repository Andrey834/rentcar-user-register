import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/car.dart';
import '../../models/fuel_expense.dart';
import '../../models/maintenance_record.dart';
import '../../providers/car_provider.dart';
import '../../services/fuel_service.dart';
import '../../services/maintenance_service.dart';
import '../../widgets/status_chip.dart';

class CarDetailScreen extends StatefulWidget {
  final String carId;
  const CarDetailScreen({super.key, required this.carId});

  @override
  State<CarDetailScreen> createState() => _CarDetailScreenState();
}

class _CarDetailScreenState extends State<CarDetailScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabs;
  final _fuelSvc = FuelService();
  final _maintSvc = MaintenanceService();
  List<FuelExpense> _fuel = [];
  List<MaintenanceRecord> _maint = [];
  bool _loadingExtra = false;

  @override
  void initState() {
    super.initState();
    _tabs = TabController(length: 3, vsync: this);
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<CarProvider>().select(widget.carId);
      _loadExtra();
    });
  }

  Future<void> _loadExtra() async {
    setState(() => _loadingExtra = true);
    try {
      final f = await _fuelSvc.getExpensesByCar(widget.carId);
      final m = await _maintSvc.getRecordsByCar(widget.carId);
      setState(() {
        _fuel = f;
        _maint = m;
      });
    } finally {
      if (mounted) setState(() => _loadingExtra = false);
    }
  }

  @override
  void dispose() {
    _tabs.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<CarProvider>(
      builder: (_, provider, __) {
        final car = provider.selected;
        if (car == null || provider.loading) {
          return Scaffold(
            appBar: AppBar(),
            body: const Center(child: CircularProgressIndicator()),
          );
        }
        return Scaffold(
          appBar: AppBar(
            title: Text(car.displayName),
            actions: [
              PopupMenuButton<CarStatus>(
                icon: const Icon(Icons.more_vert),
                onSelected: (s) => provider.updateStatus(car.id, s),
                itemBuilder: (_) => CarStatus.values
                    .where((s) => s != car.status)
                    .map(
                      (s) => PopupMenuItem(
                        value: s,
                        child: Text('Статус: ${s.label}'),
                      ),
                    )
                    .toList(),
              ),
            ],
            bottom: TabBar(
              controller: _tabs,
              tabs: const [
                Tab(text: 'Инфо'),
                Tab(text: 'Топливо'),
                Tab(text: 'ТО'),
              ],
            ),
          ),
          body: TabBarView(
            controller: _tabs,
            children: [
              _InfoTab(car: car),
              _FuelTab(fuel: _fuel, loading: _loadingExtra, carId: car.id, onAdd: _loadExtra),
              _MaintTab(records: _maint, loading: _loadingExtra, carId: car.id, onAdd: _loadExtra),
            ],
          ),
        );
      },
    );
  }
}

class _InfoTab extends StatelessWidget {
  final Car car;
  const _InfoTab({required this.car});

  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        _InfoCard(children: [
          _row('Марка / Модель', '${car.brand} ${car.model}'),
          _row('Год', '${car.year}'),
          _row('Гос. номер', car.licensePlate),
          if (car.vin != null) _row('VIN', car.vin!),
          _row('Цвет', car.color ?? '—'),
          _row('Мест', '${car.seats ?? "—"}'),
        ]),
        const SizedBox(height: 12),
        _InfoCard(children: [
          _row('Категория', car.category.label),
          _row('Топливо', car.fuelType.label),
          _row('Расход', '${car.fuelConsumptionPer100km} л/100 км'),
          _row('Трансмиссия', car.transmission?.name ?? '—'),
        ]),
        const SizedBox(height: 12),
        _InfoCard(children: [
          _row('Пробег', '${car.currentMileage} км'),
          _row('Цена аренды', '${car.dailyRate} ₽/день'),
          _row('ОСАГО до', car.insuranceExpireDate ?? '—'),
          _row('Следующее ТО', car.nextMaintenanceMileage != null
              ? '${car.nextMaintenanceMileage} км' : '—'),
        ]),
        if (car.description != null) ...[
          const SizedBox(height: 12),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                Text('Описание', style: Theme.of(context).textTheme.labelLarge),
                const SizedBox(height: 8),
                Text(car.description!),
              ]),
            ),
          ),
        ],
      ],
    );
  }

  Widget _row(String label, String value) => Padding(
        padding: const EdgeInsets.symmetric(vertical: 6),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(label, style: const TextStyle(color: Colors.grey)),
            Text(value, style: const TextStyle(fontWeight: FontWeight.w500)),
          ],
        ),
      );
}

class _InfoCard extends StatelessWidget {
  final List<Widget> children;
  const _InfoCard({required this.children});

  @override
  Widget build(BuildContext context) => Card(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          child: Column(children: children),
        ),
      );
}

class _FuelTab extends StatelessWidget {
  final List<FuelExpense> fuel;
  final bool loading;
  final String carId;
  final VoidCallback onAdd;
  const _FuelTab({required this.fuel, required this.loading, required this.carId, required this.onAdd});

  @override
  Widget build(BuildContext context) {
    if (loading) return const Center(child: CircularProgressIndicator());
    return Column(
      children: [
        Expanded(
          child: fuel.isEmpty
              ? const Center(child: Text('Записей нет'))
              : ListView.builder(
                  padding: const EdgeInsets.symmetric(vertical: 8),
                  itemCount: fuel.length,
                  itemBuilder: (_, i) {
                    final e = fuel[i];
                    return ListTile(
                      leading: const Icon(Icons.local_gas_station),
                      title: Text('${e.liters} л × ${e.pricePerLiter} ₽'),
                      subtitle: Text('${e.date}${e.station != null ? " • ${e.station}" : ""}'),
                      trailing: Text(
                        '${e.totalCost?.toStringAsFixed(0) ?? "—"} ₽',
                        style: const TextStyle(fontWeight: FontWeight.bold),
                      ),
                    );
                  },
                ),
        ),
        Padding(
          padding: const EdgeInsets.all(16),
          child: FilledButton.icon(
            onPressed: () => _showAddFuel(context),
            icon: const Icon(Icons.add),
            label: const Text('Добавить заправку'),
          ),
        ),
      ],
    );
  }

  void _showAddFuel(BuildContext context) {
    final lCtrl = TextEditingController();
    final pCtrl = TextEditingController();
    final sCtrl = TextEditingController();
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (_) => Padding(
        padding: EdgeInsets.only(
          left: 16, right: 16, top: 24,
          bottom: MediaQuery.of(context).viewInsets.bottom + 24,
        ),
        child: Column(mainAxisSize: MainAxisSize.min, children: [
          Text('Заправка', style: Theme.of(context).textTheme.titleLarge),
          const SizedBox(height: 16),
          TextField(controller: lCtrl, decoration: const InputDecoration(labelText: 'Литры'), keyboardType: TextInputType.number),
          const SizedBox(height: 12),
          TextField(controller: pCtrl, decoration: const InputDecoration(labelText: 'Цена за литр, ₽'), keyboardType: TextInputType.number),
          const SizedBox(height: 12),
          TextField(controller: sCtrl, decoration: const InputDecoration(labelText: 'Заправка (необязательно)')),
          const SizedBox(height: 20),
          FilledButton(
            onPressed: () async {
              await FuelService().addExpense(carId, {
                'date': DateTime.now().toIso8601String().substring(0, 10),
                'liters': double.parse(lCtrl.text),
                'pricePerLiter': double.parse(pCtrl.text),
                if (sCtrl.text.isNotEmpty) 'station': sCtrl.text,
              });
              if (context.mounted) Navigator.pop(context);
              onAdd();
            },
            child: const Text('Сохранить'),
          ),
        ]),
      ),
    );
  }
}

class _MaintTab extends StatelessWidget {
  final List<MaintenanceRecord> records;
  final bool loading;
  final String carId;
  final VoidCallback onAdd;
  const _MaintTab({required this.records, required this.loading, required this.carId, required this.onAdd});

  @override
  Widget build(BuildContext context) {
    if (loading) return const Center(child: CircularProgressIndicator());
    return Column(
      children: [
        Expanded(
          child: records.isEmpty
              ? const Center(child: Text('Записей нет'))
              : ListView.builder(
                  padding: const EdgeInsets.symmetric(vertical: 8),
                  itemCount: records.length,
                  itemBuilder: (_, i) {
                    final r = records[i];
                    return ListTile(
                      leading: const Icon(Icons.build_outlined),
                      title: Text(r.typeLabel),
                      subtitle: Text('${r.date}${r.provider != null ? " • ${r.provider}" : ""}'),
                      trailing: Text(
                        '${r.cost.toStringAsFixed(0)} ₽',
                        style: const TextStyle(fontWeight: FontWeight.bold),
                      ),
                    );
                  },
                ),
        ),
        Padding(
          padding: const EdgeInsets.all(16),
          child: FilledButton.icon(
            onPressed: () => _showAddMaint(context),
            icon: const Icon(Icons.add),
            label: const Text('Добавить ТО'),
          ),
        ),
      ],
    );
  }

  void _showAddMaint(BuildContext context) {
    final costCtrl = TextEditingController();
    final descCtrl = TextEditingController();
    String type = 'OIL_CHANGE';
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setSt) => Padding(
          padding: EdgeInsets.only(
            left: 16, right: 16, top: 24,
            bottom: MediaQuery.of(context).viewInsets.bottom + 24,
          ),
          child: Column(mainAxisSize: MainAxisSize.min, children: [
            Text('Запись ТО', style: Theme.of(context).textTheme.titleLarge),
            const SizedBox(height: 16),
            DropdownButtonFormField<String>(
              value: type,
              decoration: const InputDecoration(labelText: 'Тип'),
              items: const [
                DropdownMenuItem(value: 'OIL_CHANGE', child: Text('Замена масла')),
                DropdownMenuItem(value: 'TIRE_REPLACEMENT', child: Text('Замена шин')),
                DropdownMenuItem(value: 'BRAKE_SERVICE', child: Text('Тормоза')),
                DropdownMenuItem(value: 'SCHEDULED_MAINTENANCE', child: Text('Плановое ТО')),
                DropdownMenuItem(value: 'BODY_REPAIR', child: Text('Кузов')),
                DropdownMenuItem(value: 'OTHER', child: Text('Прочее')),
              ],
              onChanged: (v) => setSt(() => type = v!),
            ),
            const SizedBox(height: 12),
            TextField(controller: costCtrl, decoration: const InputDecoration(labelText: 'Стоимость, ₽'), keyboardType: TextInputType.number),
            const SizedBox(height: 12),
            TextField(controller: descCtrl, decoration: const InputDecoration(labelText: 'Описание'), maxLines: 2),
            const SizedBox(height: 20),
            FilledButton(
              onPressed: () async {
                await MaintenanceService().addRecord(carId, {
                  'date': DateTime.now().toIso8601String().substring(0, 10),
                  'type': type,
                  'cost': double.parse(costCtrl.text),
                  if (descCtrl.text.isNotEmpty) 'description': descCtrl.text,
                });
                if (context.mounted) Navigator.pop(context);
                onAdd();
              },
              child: const Text('Сохранить'),
            ),
          ]),
        ),
      ),
    );
  }
}
