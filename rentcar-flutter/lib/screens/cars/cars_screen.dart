import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/car.dart';
import '../../providers/car_provider.dart';
import '../../widgets/car_card.dart';
import '../../widgets/error_view.dart';
import 'car_detail_screen.dart';
import 'add_car_screen.dart';

class CarsScreen extends StatefulWidget {
  const CarsScreen({super.key});

  @override
  State<CarsScreen> createState() => _CarsScreenState();
}

class _CarsScreenState extends State<CarsScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabs;

  @override
  void initState() {
    super.initState();
    _tabs = TabController(length: 3, vsync: this);
    WidgetsBinding.instance.addPostFrameCallback(
      (_) => context.read<CarProvider>().loadAll(),
    );
  }

  @override
  void dispose() {
    _tabs.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Автопарк'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () => context.read<CarProvider>().loadAll(),
          ),
        ],
        bottom: TabBar(
          controller: _tabs,
          tabs: const [
            Tab(text: 'Все'),
            Tab(text: 'Доступны'),
            Tab(text: 'Нужно ТО'),
          ],
        ),
      ),
      body: Consumer<CarProvider>(
        builder: (_, provider, __) {
          if (provider.loading) {
            return const Center(child: CircularProgressIndicator());
          }
          if (provider.error != null && provider.cars.isEmpty) {
            return ErrorView(
              message: provider.error!,
              onRetry: () => provider.loadAll(),
            );
          }
          return TabBarView(
            controller: _tabs,
            children: [
              _CarList(cars: provider.cars),
              _CarList(cars: provider.available),
              _CarList(cars: provider.needsMaintenance),
            ],
          );
        },
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => Navigator.push(
          context,
          MaterialPageRoute(builder: (_) => const AddCarScreen()),
        ),
        icon: const Icon(Icons.add),
        label: const Text('Добавить'),
      ),
    );
  }
}

class _CarList extends StatelessWidget {
  final List<Car> cars;
  const _CarList({required this.cars});

  @override
  Widget build(BuildContext context) {
    if (cars.isEmpty) {
      return const Center(child: Text('Нет автомобилей'));
    }
    return RefreshIndicator(
      onRefresh: () => context.read<CarProvider>().loadAll(),
      child: ListView.builder(
        padding: const EdgeInsets.only(top: 8, bottom: 80),
        itemCount: cars.length,
        itemBuilder: (_, i) => CarCard(
          car: cars[i],
          onTap: () => Navigator.push(
            context,
            MaterialPageRoute(
              builder: (_) => CarDetailScreen(carId: cars[i].id),
            ),
          ),
        ),
      ),
    );
  }
}
