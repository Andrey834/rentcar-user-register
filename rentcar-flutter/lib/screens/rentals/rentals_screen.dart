import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/rental.dart';
import '../../providers/rental_provider.dart';
import '../../widgets/rental_card.dart';
import '../../widgets/error_view.dart';
import 'rental_detail_screen.dart';
import 'create_rental_screen.dart';

class RentalsScreen extends StatefulWidget {
  const RentalsScreen({super.key});

  @override
  State<RentalsScreen> createState() => _RentalsScreenState();
}

class _RentalsScreenState extends State<RentalsScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabs;
  final _statuses = [
    RentalStatus.pending,
    RentalStatus.active,
    RentalStatus.completed,
    RentalStatus.overdue,
  ];

  @override
  void initState() {
    super.initState();
    _tabs = TabController(length: 4, vsync: this);
    _tabs.addListener(() {
      if (!_tabs.indexIsChanging) _loadForTab(_tabs.index);
    });
    WidgetsBinding.instance.addPostFrameCallback(
      (_) => _loadForTab(0),
    );
  }

  void _loadForTab(int index) {
    final p = context.read<RentalProvider>();
    if (index == 3) {
      p.loadOverdue();
    } else {
      p.loadByStatus(_statuses[index]);
    }
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
        title: const Text('Аренды'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () => _loadForTab(_tabs.index),
          ),
        ],
        bottom: TabBar(
          controller: _tabs,
          isScrollable: true,
          tabs: const [
            Tab(text: 'Ожидают'),
            Tab(text: 'Активные'),
            Tab(text: 'Завершены'),
            Tab(text: 'Просрочены'),
          ],
        ),
      ),
      body: Consumer<RentalProvider>(
        builder: (_, provider, __) {
          if (provider.loading) {
            return const Center(child: CircularProgressIndicator());
          }
          if (provider.error != null && provider.rentals.isEmpty) {
            return ErrorView(
              message: provider.error!,
              onRetry: () => _loadForTab(_tabs.index),
            );
          }
          return TabBarView(
            controller: _tabs,
            children: List.generate(4, (_) => _RentalList(rentals: provider.rentals)),
          );
        },
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => Navigator.push(
          context,
          MaterialPageRoute(builder: (_) => const CreateRentalScreen()),
        ),
        icon: const Icon(Icons.add),
        label: const Text('Забронировать'),
      ),
    );
  }
}

class _RentalList extends StatelessWidget {
  final List<Rental> rentals;
  const _RentalList({required this.rentals});

  @override
  Widget build(BuildContext context) {
    if (rentals.isEmpty) {
      return const Center(child: Text('Аренд нет'));
    }
    return ListView.builder(
      padding: const EdgeInsets.only(top: 8, bottom: 80),
      itemCount: rentals.length,
      itemBuilder: (_, i) => RentalCard(
        rental: rentals[i],
        onTap: () => Navigator.push(
          context,
          MaterialPageRoute(
            builder: (_) => RentalDetailScreen(rentalId: rentals[i].id),
          ),
        ),
      ),
    );
  }
}
