import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/driver_license.dart';
import '../../providers/driver_provider.dart';
import '../../widgets/error_view.dart';
import '../../widgets/status_chip.dart';
import 'add_license_screen.dart';

class DriversScreen extends StatefulWidget {
  const DriversScreen({super.key});

  @override
  State<DriversScreen> createState() => _DriversScreenState();
}

class _DriversScreenState extends State<DriversScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback(
      (_) => context.read<DriverProvider>().loadAll(),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Водители'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () => context.read<DriverProvider>().loadAll(),
          ),
        ],
      ),
      body: Consumer<DriverProvider>(
        builder: (_, provider, __) {
          if (provider.loading) {
            return const Center(child: CircularProgressIndicator());
          }
          if (provider.error != null && provider.drivers.isEmpty) {
            return ErrorView(
              message: provider.error!,
              onRetry: () => provider.loadAll(),
            );
          }
          if (provider.drivers.isEmpty) {
            return const Center(child: Text('Водители не зарегистрированы'));
          }
          return RefreshIndicator(
            onRefresh: () => provider.loadAll(),
            child: ListView.builder(
              padding: const EdgeInsets.only(top: 8, bottom: 80),
              itemCount: provider.drivers.length,
              itemBuilder: (_, i) => _DriverCard(driver: provider.drivers[i]),
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => Navigator.push(
          context,
          MaterialPageRoute(builder: (_) => const AddLicenseScreen()),
        ),
        icon: const Icon(Icons.badge_outlined),
        label: const Text('Добавить ВУ'),
      ),
    );
  }
}

class _DriverCard extends StatelessWidget {
  final DriverLicense driver;
  const _DriverCard({required this.driver});

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Row(children: [
            const Icon(Icons.person),
            const SizedBox(width: 8),
            Expanded(
              child: Text(
                driver.driverName,
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
              ),
            ),
            StatusChip(
              label: driver.valid ? 'Действует' : 'Просрочено',
              color: driver.valid ? Colors.green : Colors.red,
            ),
          ]),
          const SizedBox(height: 8),
          Row(children: [
            Icon(Icons.badge_outlined, size: 14, color: cs.onSurfaceVariant),
            const SizedBox(width: 4),
            Text(driver.licenseNumber,
                style: TextStyle(color: cs.onSurfaceVariant, fontSize: 13)),
            const SizedBox(width: 16),
            Icon(Icons.category_outlined, size: 14, color: cs.onSurfaceVariant),
            const SizedBox(width: 4),
            Text('Кат. ${driver.category}',
                style: TextStyle(color: cs.onSurfaceVariant, fontSize: 13)),
          ]),
          const SizedBox(height: 6),
          Row(children: [
            Icon(Icons.star, size: 14, color: Colors.amber),
            const SizedBox(width: 4),
            Text('${driver.rating.toStringAsFixed(1)}',
                style: const TextStyle(fontSize: 13)),
            const SizedBox(width: 16),
            Icon(Icons.history, size: 14, color: cs.onSurfaceVariant),
            const SizedBox(width: 4),
            Text('${driver.completedRentals} аренд',
                style: TextStyle(color: cs.onSurfaceVariant, fontSize: 13)),
            const Spacer(),
            Text('до ${driver.expireDate}',
                style: TextStyle(color: cs.onSurfaceVariant, fontSize: 12)),
          ]),
        ]),
      ),
    );
  }
}
