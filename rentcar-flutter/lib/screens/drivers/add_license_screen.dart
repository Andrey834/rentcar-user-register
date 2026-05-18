import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../providers/driver_provider.dart';

class AddLicenseScreen extends StatefulWidget {
  const AddLicenseScreen({super.key});

  @override
  State<AddLicenseScreen> createState() => _AddLicenseScreenState();
}

class _AddLicenseScreenState extends State<AddLicenseScreen> {
  final _form = GlobalKey<FormState>();
  final _personId = TextEditingController();
  final _number = TextEditingController();
  final _exp = TextEditingController();
  final _years = TextEditingController(text: '3');
  String _category = 'B';
  DateTime _issueDate = DateTime.now().subtract(const Duration(days: 365));
  DateTime _expireDate = DateTime.now().add(const Duration(days: 3650));
  bool _saving = false;

  @override
  void dispose() {
    _personId.dispose();
    _number.dispose();
    _exp.dispose();
    _years.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Зарегистрировать ВУ')),
      body: Form(
        key: _form,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            TextFormField(
              controller: _personId,
              decoration: const InputDecoration(
                labelText: 'ID пользователя (UUID)',
                hintText: 'Скопируйте из профиля',
              ),
              validator: (v) =>
                  (v == null || v.isEmpty) ? 'Обязательное поле' : null,
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _number,
              decoration: const InputDecoration(labelText: 'Номер ВУ'),
              validator: (v) =>
                  (v == null || v.isEmpty) ? 'Обязательное поле' : null,
            ),
            const SizedBox(height: 12),
            DropdownButtonFormField<String>(
              value: _category,
              decoration: const InputDecoration(labelText: 'Категория'),
              items: ['A', 'B', 'C', 'D', 'E', 'BE', 'CE']
                  .map((c) => DropdownMenuItem(value: c, child: Text(c)))
                  .toList(),
              onChanged: (v) => setState(() => _category = v!),
            ),
            const SizedBox(height: 12),
            Row(children: [
              Expanded(
                child: _DateField(
                  label: 'Дата выдачи',
                  date: _issueDate,
                  onPick: (d) => setState(() => _issueDate = d),
                  lastDate: DateTime.now(),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _DateField(
                  label: 'Действует до',
                  date: _expireDate,
                  onPick: (d) => setState(() => _expireDate = d),
                  firstDate: DateTime.now(),
                ),
              ),
            ]),
            const SizedBox(height: 12),
            TextFormField(
              controller: _years,
              decoration: const InputDecoration(labelText: 'Стаж вождения, лет'),
              keyboardType: TextInputType.number,
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
                  : const Text('Зарегистрировать'),
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
    final ok = await context.read<DriverProvider>().registerLicense({
      'personId': _personId.text.trim(),
      'licenseNumber': _number.text.trim(),
      'issueDate': _fmt(_issueDate),
      'expireDate': _fmt(_expireDate),
      'category': _category,
      if (_years.text.isNotEmpty) 'experienceYears': int.parse(_years.text),
    });
    if (mounted) {
      setState(() => _saving = false);
      if (ok) {
        Navigator.pop(context);
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(context.read<DriverProvider>().error ?? 'Ошибка'),
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
  final DateTime? lastDate;

  const _DateField({
    required this.label,
    required this.date,
    required this.onPick,
    this.firstDate,
    this.lastDate,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: () async {
        final d = await showDatePicker(
          context: context,
          initialDate: date,
          firstDate: firstDate ?? DateTime(1990),
          lastDate: lastDate ?? DateTime(2050),
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
