# RentCar — Система управления арендой автомобилей

Полнофункциональный REST API сервис для управления бизнесом по прокату автомобилей.

---

## Возможности

| Модуль | Описание |
|---|---|
| **Регистрация пользователей** | Регистрация с подтверждением по email |
| **Управление автопарком** | Добавление, редактирование, статусы, поиск по категории/доступности |
| **Водительские удостоверения** | Регистрация ВУ, валидация срока действия, рейтинг водителей |
| **Аренда** | Бронирование, выдача, возврат, проверка конфликтов дат |
| **Расходы на топливо** | Учёт заправок по автомобилям и периодам |
| **ТО и ремонт** | История обслуживания, напоминания о плановом ТО |
| **Система скидок** | Промокоды, скидки за лояльность, сезонные и долгосрочные |
| **Отчётность** | Финансовые отчёты, доходность по каждому автомобилю |

---

## Технологии

- **Java 21**
- **Spring Boot 3.2**
- **Spring Data JPA / Hibernate**
- **PostgreSQL**
- **Spring Mail** — отправка писем с подтверждением
- **Spring Validation** — валидация входных данных
- **Springdoc OpenAPI (Swagger UI)**
- **Lombok**
- **Spring Security Crypto** — BCrypt-хеширование паролей
- **Docker / Docker Compose**

---

## Быстрый старт

### Через Docker Compose

```bash
# Скопируйте и настройте переменные окружения
cp .env.example .env

# Запуск всех сервисов
docker compose up -d

# Приложение будет доступно на http://localhost:8080
```

### Локально (Maven)

```bash
# Требуется запущенный PostgreSQL

export POSTGRES_URL=jdbc:postgresql://localhost:5432/rentcar
export POSTGRES_USER=rentcar_user
export POSTGRES_PASSWORD=rentcar_pass

./mvnw spring-boot:run
```

---

## Переменные окружения

| Переменная | Описание | Пример |
|---|---|---|
| `POSTGRES_URL` | JDBC URL базы данных | `jdbc:postgresql://localhost:5432/rentcar` |
| `POSTGRES_USER` | Имя пользователя БД | `rentcar_user` |
| `POSTGRES_PASSWORD` | Пароль БД | `secret` |
| `MAIL_HOST` | SMTP-сервер | `smtp.gmail.com` |
| `MAIL_PORT` | Порт SMTP | `587` |
| `MAIL_USERNAME` | Логин почты | `noreply@yourcompany.com` |
| `MAIL_PASSWORD` | Пароль / App Password | `app_password` |

---

## API — Документация

После запуска Swagger UI доступен по адресу:

```
http://localhost:8080/swagger-ui.html
```

### Основные эндпоинты

#### Пользователи
| Метод | URL | Описание |
|---|---|---|
| `POST` | `/users/register` | Регистрация нового пользователя |

#### Автомобили
| Метод | URL | Описание |
|---|---|---|
| `GET` | `/api/v1/cars` | Список всех автомобилей (фильтры: `status`, `category`) |
| `GET` | `/api/v1/cars/available?startDate=&endDate=` | Доступные на период |
| `GET` | `/api/v1/cars/maintenance-needed` | Требующие ТО |
| `POST` | `/api/v1/cars` | Добавить автомобиль |
| `PUT` | `/api/v1/cars/{id}` | Обновить данные |
| `PATCH` | `/api/v1/cars/{id}/status` | Изменить статус |
| `DELETE` | `/api/v1/cars/{id}` | Удалить |

#### Водители
| Метод | URL | Описание |
|---|---|---|
| `GET` | `/api/v1/drivers` | Все водители |
| `POST` | `/api/v1/drivers/license` | Зарегистрировать ВУ |
| `GET` | `/api/v1/drivers/{personId}/license` | ВУ водителя |
| `PUT` | `/api/v1/drivers/license/{licenseId}` | Обновить ВУ |

#### Аренда
| Метод | URL | Описание |
|---|---|---|
| `POST` | `/api/v1/rentals` | Создать бронирование |
| `PATCH` | `/api/v1/rentals/{id}/start` | Выдать автомобиль |
| `PATCH` | `/api/v1/rentals/{id}/complete` | Принять автомобиль |
| `PATCH` | `/api/v1/rentals/{id}/cancel` | Отменить |
| `GET` | `/api/v1/rentals/overdue` | Просроченные аренды |
| `GET` | `/api/v1/rentals?driverId=` | Аренды водителя |
| `GET` | `/api/v1/rentals?carId=` | Аренды автомобиля |

#### Расходы на топливо
| Метод | URL | Описание |
|---|---|---|
| `POST` | `/api/v1/fuel-expenses/cars/{carId}` | Записать заправку |
| `GET` | `/api/v1/fuel-expenses/cars/{carId}` | Заправки по автомобилю |
| `GET` | `/api/v1/fuel-expenses?from=&to=` | За период |

#### Техническое обслуживание
| Метод | URL | Описание |
|---|---|---|
| `POST` | `/api/v1/maintenance/cars/{carId}` | Добавить запись ТО |
| `GET` | `/api/v1/maintenance/cars/{carId}` | История ТО автомобиля |
| `GET` | `/api/v1/maintenance?from=&to=` | За период |

#### Скидки
| Метод | URL | Описание |
|---|---|---|
| `POST` | `/api/v1/discounts` | Создать скидку |
| `GET` | `/api/v1/discounts` | Все скидки (`?activeOnly=true`) |
| `GET` | `/api/v1/discounts/validate?promoCode=&driverId=&rentalDays=` | Проверить промокод |
| `GET` | `/api/v1/discounts/loyalty/{driverId}` | Скидка лояльности |

#### Отчёты
| Метод | URL | Описание |
|---|---|---|
| `GET` | `/api/v1/reports/financial?from=&to=` | Финансовый отчёт |
| `GET` | `/api/v1/reports/cars/{carId}/summary` | Отчёт по автомобилю |
| `GET` | `/api/v1/reports/cars/summary` | Отчёт по всему парку |

---

## Система скидок

| Тип | Описание |
|---|---|
| `PROMO_CODE` | Промокод с ограниченным сроком действия |
| `LOYALTY` | Автоматическая скидка за количество завершённых аренд |
| `LONG_TERM` | Автоматически 15% при аренде от 30 дней |
| `SEASONAL` | Сезонные акции |
| `CORPORATE` | Корпоративные клиенты |
| `FIRST_RENTAL` | Скидка на первую аренду |

---

## Жизненный цикл аренды

```
PENDING → ACTIVE → COMPLETED
    ↓          ↓
CANCELLED   CANCELLED
```

---

## Запуск тестов

```bash
./mvnw test
```

---

## Сборка Docker-образа

```bash
./mvnw clean package -DskipTests
docker build -t rentcar-app .
```

---

## Actuator

| Эндпоинт | Описание |
|---|---|
| `GET /actuator/health` | Состояние приложения |
| `GET /actuator/info` | Информация |
| `GET /actuator/metrics` | Метрики |
