import 'package:flutter/material.dart';
import '../models/rental.dart';
import 'status_chip.dart';

class RentalCard extends StatelessWidget {
  final Rental rental;
  final VoidCallback? onTap;

  const RentalCard({super.key, required this.rental, this.onTap});

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
      child: InkWell(
        borderRadius: BorderRadius.circular(12),
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Expanded(
                    child: Text(
                      rental.carInfo,
                      style: Theme.of(context)
                          .textTheme
                          .titleMedium
                          ?.copyWith(fontWeight: FontWeight.bold),
                    ),
                  ),
                  StatusChip(
                    label: rental.status.label,
                    color: rental.status.color,
                  ),
                ],
              ),
              const SizedBox(height: 6),
              Row(
                children: [
                  Icon(Icons.person_outline,
                      size: 14, color: cs.onSurfaceVariant),
                  const SizedBox(width: 4),
                  Text(rental.driverName,
                      style: TextStyle(
                          color: cs.onSurfaceVariant, fontSize: 13)),
                ],
              ),
              const SizedBox(height: 6),
              Row(
                children: [
                  Icon(Icons.calendar_today_outlined,
                      size: 14, color: cs.onSurfaceVariant),
                  const SizedBox(width: 4),
                  Text(
                    '${rental.plannedStartDate} → ${rental.plannedEndDate}',
                    style:
                        TextStyle(color: cs.onSurfaceVariant, fontSize: 13),
                  ),
                  const Spacer(),
                  Text(
                    '${rental.plannedDays} дн.',
                    style: TextStyle(
                        color: cs.onSurfaceVariant,
                        fontSize: 13,
                        fontWeight: FontWeight.w500),
                  ),
                ],
              ),
              if (rental.totalAmount != null) ...[
                const SizedBox(height: 8),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    if (rental.discountPercent > 0)
                      Text(
                        'Скидка ${rental.discountPercent.toStringAsFixed(0)}%',
                        style: TextStyle(color: Colors.green, fontSize: 12),
                      )
                    else
                      const SizedBox.shrink(),
                    Text(
                      '${rental.totalAmount!.toStringAsFixed(0)} ₽',
                      style: TextStyle(
                        color: cs.primary,
                        fontWeight: FontWeight.bold,
                        fontSize: 16,
                      ),
                    ),
                  ],
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }
}
