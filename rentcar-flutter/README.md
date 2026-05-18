# RentCar Mobile

Мобильное приложение для управления бизнесом по аренде автомобилей.  
Написано на Flutter, работает на Android и iOS.

---

## Скриншоты

| Автопарк | Аренда | Водители | Отчёты |
|:---:|:---:|:---:|:---:|
| Список авто с фильтрами | Бронирование и выдача | ВУ и рейтинг | Финансы и расходы |

---

## Возможности

### Автопарк
- Список всех автомобилей с фильтрацией по статусу
- Быстрый поиск машин, доступных на нужный период
- Вкладка «Нужно ТО» — авто, у которых истёк интервал обслуживания
- Детальная карточка: технические характеристики, история заправок и ТО
- Добавление новых автомобилей через форму
- Смена статуса (Доступен / В аренде / ТО / Списан) прямо из карточки

### Аренды
- Фильтрация по статусам: Ожидают / Активные / Завершены / Просрочены
- Форма бронирования с выбором авто, водителя, дат и промокода
- Автоматический расчёт стоимости с учётом скидок
- Выдача автомобиля — фиксация пробега и уровня топлива
- Приём автомобиля — расчёт компенсации за недолитое топливо
- Отмена бронирования

### Водители
- Реестр водительских удостоверений
- Отображение рейтинга, стажа и количества завершённых аренд
- Индикатор срока действия ВУ
- Регистрация нового водительского удостоверения

### Отчёты
- Финансовый отчёт за произвольный период
- Выручка, расходы на топливо и ТО, чистая прибыль
- Сводка по каждому автомобилю: доходность, расходы, пробег
- Статистика автопарка в реальном времени

---

## Стек

| Слой | Решение |
|---|---|
| UI | Flutter 3 + Material Design 3 |
| Состояние | Provider |
| HTTP | Dio |
| Тема | Светлая / Тёмная (system) |
| Минимальный SDK | Android 5.0 (API 21) / iOS 12 |

---

## Быстрый старт

### Требования

- [Flutter SDK](https://docs.flutter.dev/get-started/install) `>= 3.2.0`
- Запущенный бэкенд RentCar (см. корневой `README.md`)

### Установка и запуск

```bash
# Перейти в папку проекта
cd rentcar-flutter

# Установить зависимости
flutter pub get

# Запустить на подключённом устройстве или эмуляторе
flutter run
```

### Сборка релизного APK

```bash
flutter build apk --release
# Файл: build/app/outputs/flutter-apk/app-release.apk
```

### Сборка для iOS

```bash
flutter build ios --release
```

---

## Настройка подключения к API

Откройте `lib/config/api_config.dart` и укажите адрес бэкенда:

```dart
class ApiConfig {
  // Android-эмулятор (localhost хост-машины)
  static const String baseUrl = 'http://10.0.2.2:8080';

  // iOS-симулятор или веб
  // static const String baseUrl = 'http://localhost:8080';

  // Реальное устройство (IP вашей машины в локальной сети)
  // static const String baseUrl = 'http://192.168.1.100:8080';
}
```

---

## Архитектура

```
lib/
├── main.dart                        # Точка входа, MultiProvider
├── config/
│   └── api_config.dart              # Базовый URL и пути эндпоинтов
│
├── models/                          # Модели данных с JSON-сериализацией
│   ├── car.dart
│   ├── rental.dart
│   ├── driver_license.dart
│   ├── fuel_expense.dart
│   ├── maintenance_record.dart
│   └── report.dart
│
├── services/                        # HTTP-сервисы (Dio)
│   ├── api_client.dart              # Singleton-клиент с логированием
│   ├── car_service.dart
│   ├── rental_service.dart
│   ├── driver_service.dart
│   ├── fuel_service.dart
│   ├── maintenance_service.dart
│   └── report_service.dart
│
├── providers/                       # Управление состоянием (Provider)
│   ├── car_provider.dart
│   ├── rental_provider.dart
│   ├── driver_provider.dart
│   └── report_provider.dart
│
├── screens/                         # Экраны приложения
│   ├── main_screen.dart             # Bottom NavigationBar
│   ├── cars/
│   │   ├── cars_screen.dart         # Список с вкладками
│   │   ├── car_detail_screen.dart   # Детали + топливо + ТО
│   │   └── add_car_screen.dart      # Форма добавления
│   ├── rentals/
│   │   ├── rentals_screen.dart      # Фильтрованный список
│   │   ├── rental_detail_screen.dart# Детали + действия
│   │   └── create_rental_screen.dart# Бронирование
│   ├── drivers/
│   │   ├── drivers_screen.dart      # Реестр водителей
│   │   └── add_license_screen.dart  # Регистрация ВУ
│   └── reports/
│       └── reports_screen.dart      # Финансы + авто
│
├── widgets/                         # Переиспользуемые компоненты
│   ├── car_card.dart
│   ├── rental_card.dart
│   ├── status_chip.dart
│   └── error_view.dart
│
└── theme/
    └── app_theme.dart               # Material 3, светлая и тёмная тема
```

### Поток данных

```
UI (Screen)
    │  читает/слушает
    ▼
Provider  ──►  Service  ──►  ApiClient (Dio)  ──►  REST API
    ▲               │
    └───────────────┘
         notifyListeners()
```

---

## Зависимости

```yaml
provider: ^6.1.2        # Управление состоянием
dio: ^5.4.0             # HTTP-клиент
intl: ^0.19.0           # Форматирование дат
cupertino_icons: ^1.0.8 # Иконки
```

---

## Связь с бэкендом

Клиент работает с REST API из модуля `rentcar-user-register`. Убедитесь, что бэкенд запущен и доступен по настроенному адресу.

```bash
# Запуск бэкенда через Docker Compose (из корня репозитория)
docker compose up -d
```

Документация API доступна на `http://localhost:8080/swagger-ui.html`.

---

## Поддерживаемые платформы

| Платформа | Поддержка |
|---|---|
| Android | ✅ |
| iOS | ✅ |
| Web | ⚠️ Работает, требует настройки CORS на бэкенде |
| macOS / Windows / Linux | ⚠️ Требует дополнительной конфигурации |
