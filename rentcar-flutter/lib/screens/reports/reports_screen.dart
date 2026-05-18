import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/report.dart';
import '../../providers/report_provider.dart';
import '../../widgets/error_view.dart';

class ReportsScreen extends StatefulWidget {
  const ReportsScreen({super.key});

  @override
  State<ReportsScreen> createState() => _ReportsScreenState();
}

class _ReportsScreenState extends State<ReportsScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabs;
  DateTime _from = DateTime.now().subtract(const Duration(days: 30));
  DateTime _to = DateTime.now();

  @override
  void initState() {
    super.initState();
    _tabs = TabController(length: 2, vsync: this);
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _loadReport();
      context.read<ReportProvider>().loadCarSummaries();
    });
  }

  void _loadReport() {
    context.read<ReportProvider>().loadFinancialReport(
          _fmt(_from),
          _fmt(_to),
        );
  }

  String _fmt(DateTime d) =>
      '${d.year}-${d.month.toString().padLeft(2, '0')}-${d.day.toString().padLeft(2, '0')}';

  String _fmtDisplay(DateTime d) =>
      '${d.day.toString().padLeft(2, '0')}.${d.month.toString().padLeft(2, '0')}.${d.year}';

  @override
  void dispose() {
    _tabs.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Отчёты'),
        bottom: TabBar(
          controller: _tabs,
          tabs: const [
            Tab(text: 'Финансы'),
            Tab(text: 'По автомобилям'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabs,
        children: [
          _FinancialTab(
            from: _from,
            to: _to,
            onDateChanged: (f, t) {
              setState(() { _from = f; _to = t; });
              _loadReport();
            },
            fmtDisplay: _fmtDisplay,
          ),
          const _CarsTab(),
        ],
      ),
    );
  }
}

class _FinancialTab extends StatelessWidget {
  final DateTime from;
  final DateTime to;
  final void Function(DateTime, DateTime) onDateChanged;
  final String Function(DateTime) fmtDisplay;

  const _FinancialTab({
    required this.from,
    required this.to,
    required this.onDateChanged,
    required this.fmtDisplay,
  });

  @override
  Widget build(BuildContext context) {
    return Consumer<ReportProvider>(
      builder: (_, provider, __) {
        return Column(
          children: [
            _PeriodSelector(from: from, to: to, onChanged: onDateChanged, fmtDisplay: fmtDisplay),
            if (provider.loading)
              const Expanded(child: Center(child: CircularProgressIndicator()))
            else if (provider.error != null && provider.report == null)
              Expanded(child: ErrorView(message: provider.error!, onRetry: () {}))
            else if (provider.report == null)
              const Expanded(child: Center(child: Text('Нет данных')))
            else
              Expanded(child: _ReportBody(r: provider.report!)),
          ],
        );
      },
    );
  }
}

class _PeriodSelector extends StatelessWidget {
  final DateTime from;
  final DateTime to;
  final void Function(DateTime, DateTime) onChanged;
  final String Function(DateTime) fmtDisplay;

  const _PeriodSelector({
    required this.from,
    required this.to,
    required this.onChanged,
    required this.fmtDisplay,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(12),
      child: Row(
        children: [
          Expanded(
            child: OutlinedButton.icon(
              icon: const Icon(Icons.calendar_today, size: 16),
              label: Text(fmtDisplay(from)),
              onPressed: () async {
                final d = await showDatePicker(
                  context: context,
                  initialDate: from,
                  firstDate: DateTime(2020),
                  lastDate: to,
                );
                if (d != null) onChanged(d, to);
              },
            ),
          ),
          const Padding(padding: EdgeInsets.symmetric(horizontal: 8), child: Text('—')),
          Expanded(
            child: OutlinedButton.icon(
              icon: const Icon(Icons.calendar_today, size: 16),
              label: Text(fmtDisplay(to)),
              onPressed: () async {
                final d = await showDatePicker(
                  context: context,
                  initialDate: to,
                  firstDate: from,
                  lastDate: DateTime.now(),
                );
                if (d != null) onChanged(from, d);
              },
            ),
          ),
        ],
      ),
    );
  }
}

class _ReportBody extends StatelessWidget {
  final FinancialReport r;
  const _ReportBody({required this.r});

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Row(children: [
          Expanded(child: _MetricCard(label: 'Выручка', value: '${_fmt(r.totalRevenue)} ₽', color: Colors.green)),
          const SizedBox(width: 12),
          Expanded(child: _MetricCard(label: 'Прибыль', value: '${_fmt(r.netProfit)} ₽', color: r.netProfit >= 0 ? Colors.blue : Colors.red)),
        ]),
        const SizedBox(height: 12),
        Row(children: [
          Expanded(child: _MetricCard(label: 'Топливо', value: '${_fmt(r.totalFuelExpenses)} ₽', color: Colors.orange)),
          const SizedBox(width: 12),
          Expanded(child: _MetricCard(label: 'ТО/Ремонт', value: '${_fmt(r.totalMaintenanceCosts)} ₽', color: Colors.deepPurple)),
        ]),
        const SizedBox(height: 16),
        Card(child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(children: [
            _row('Всего аренд', '${r.totalRentals}'),
            _row('Завершено', '${r.completedRentals}'),
            _row('Отменено', '${r.cancelledRentals}'),
            _row('Средняя длит.', '${r.averageRentalDays.toStringAsFixed(1)} дн.'),
            _row('Пробег за период', '${r.totalMileageDriven} км'),
          ]),
        )),
        const SizedBox(height: 12),
        Card(child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text('Автопарк', style: Theme.of(context).textTheme.labelLarge),
            const SizedBox(height: 12),
            Row(mainAxisAlignment: MainAxisAlignment.spaceAround, children: [
              _FleetItem(label: 'Свободны', count: r.totalActiveCars, color: Colors.green),
              _FleetItem(label: 'В аренде', count: r.totalRentedCars, color: Colors.blue),
              _FleetItem(label: 'На ТО', count: r.totalMaintenanceCars, color: Colors.orange),
            ]),
          ]),
        )),
      ],
    );
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

  String _fmt(double v) => v.toStringAsFixed(0).replaceAllMapped(
        RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'),
        (m) => '${m[1]} ',
      );
}

class _MetricCard extends StatelessWidget {
  final String label;
  final String value;
  final Color color;
  const _MetricCard({required this.label, required this.value, required this.color});

  @override
  Widget build(BuildContext context) => Card(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text(label, style: TextStyle(color: Colors.grey, fontSize: 12)),
            const SizedBox(height: 4),
            Text(value,
                style: TextStyle(
                    color: color, fontSize: 18, fontWeight: FontWeight.bold)),
          ]),
        ),
      );
}

class _FleetItem extends StatelessWidget {
  final String label;
  final int count;
  final Color color;
  const _FleetItem({required this.label, required this.count, required this.color});

  @override
  Widget build(BuildContext context) => Column(children: [
        Text('$count',
            style: TextStyle(
                fontSize: 28, fontWeight: FontWeight.bold, color: color)),
        Text(label, style: TextStyle(color: Colors.grey, fontSize: 12)),
      ]);
}

class _CarsTab extends StatelessWidget {
  const _CarsTab();

  @override
  Widget build(BuildContext context) {
    return Consumer<ReportProvider>(
      builder: (_, provider, __) {
        if (provider.loading) {
          return const Center(child: CircularProgressIndicator());
        }
        if (provider.carSummaries.isEmpty) {
          return const Center(child: Text('Нет данных'));
        }
        return ListView.builder(
          padding: const EdgeInsets.all(12),
          itemCount: provider.carSummaries.length,
          itemBuilder: (_, i) => _CarSummaryCard(s: provider.carSummaries[i]),
        );
      },
    );
  }
}

class _CarSummaryCard extends StatelessWidget {
  final CarSummary s;
  const _CarSummaryCard({required this.s});

  @override
  Widget build(BuildContext context) {
    final profitable = s.netProfit >= 0;
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: Padding(
        padding: const EdgeInsets.all(14),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Row(children: [
            Expanded(
              child: Text(
                '${s.carInfo} • ${s.licensePlate}',
                style: Theme.of(context).textTheme.titleSmall,
              ),
            ),
            Text(
              '${s.netProfit >= 0 ? "+" : ""}${s.netProfit.toStringAsFixed(0)} ₽',
              style: TextStyle(
                fontWeight: FontWeight.bold,
                color: profitable ? Colors.green : Colors.red,
              ),
            ),
          ]),
          const SizedBox(height: 8),
          Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
            _mini('Выручка', '${s.totalRevenue.toStringAsFixed(0)} ₽', Colors.green),
            _mini('Расходы', '${s.totalExpenses.toStringAsFixed(0)} ₽', Colors.red),
            _mini('Аренд', '${s.totalRentals}', Colors.blue),
            _mini('Пробег', '${s.currentMileage} км', Colors.grey),
          ]),
        ]),
      ),
    );
  }

  Widget _mini(String label, String value, Color color) => Column(children: [
        Text(value, style: TextStyle(fontWeight: FontWeight.bold, color: color, fontSize: 13)),
        Text(label, style: const TextStyle(color: Colors.grey, fontSize: 11)),
      ]);
}
