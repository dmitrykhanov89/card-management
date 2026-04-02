# Card Management Service

REST-сервис для управления банковскими картами. Поддерживает создание и управление картами, переводы между картами, аутентификацию и авторизацию пользователей через JWT.

## Технологический стек

- **Java 21**, **Spring Boot 4.0.5**
- **Spring Security** + JWT (jjwt 0.11.5)
- **Spring Data JPA** + **PostgreSQL 15**
- **Liquibase** — миграции базы данных
- **Swagger / OpenAPI** (springdoc-openapi 2.8.13) — документация API
- **Lombok** — сокращение шаблонного кода
- **Docker / Docker Compose** — контейнеризация
- **Gradle** — сборка проекта

## Запуск

### Docker Compose (рекомендуемый способ)

```bash
./gradlew build -x test
docker compose up --build
```

Приложение будет доступно по адресу `http://localhost:8080`.

### Локальный запуск

1. Запустите PostgreSQL (например, через Docker) и создать новую базу данных с названием card_db:

```bash
docker compose up db
```

2. Запустите приложение:

```bash
./gradlew bootRun
```

### Переменные окружения

| Переменная | По умолчанию | Описание |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/card_db` | URL подключения к БД |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Пользователь БД |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` | Пароль БД |

## Структура базы данных

Схема создаётся автоматически через Liquibase-миграции при старте приложения.

### Таблицы

- **users** — пользователи (`id`, `username`, `password`)
- **roles** — роли (`id`, `name`). Предзаполнены: `ROLE_USER`, `ROLE_ADMIN`
- **user_roles** — связь пользователей и ролей (many-to-many)
- **cards** — банковские карты (`id`, `card_number`, `expiration_date`, `status`, `balance`, `block_requested`, `owner_id`)
- **transfers** — переводы (`id`, `from_card_id`, `to_card_id`, `amount`, `created_at`)

При первом запуске автоматически создаётся администратор и пользователь:
- Логин: `admin`
- Пароль: `admin123`
-
- Логин: `user`
- Пароль: `user123`

## Роли и права доступа

| Роль | Описание |
|---|---|
| `USER` | Просмотр своих карт и баланса, переводы между своими картами, запрос блокировки карты |
| `ADMIN` | Полное управление картами и пользователями: создание, удаление, изменение статуса, просмотр всех пользователей |

## API

### Аутентификация (`/api/auth`)

| Метод | Путь | Доступ | Описание |
|---|---|---|---|
| POST | `/api/auth/register` | Открытый | Регистрация нового пользователя (роль USER) |
| POST | `/api/auth/login` | Открытый | Авторизация, возвращает JWT-токен |
| POST | `/api/auth/admin-register` | ADMIN | Регистрация администратора |

**Регистрация:**
```json
POST /api/auth/register
{
  "username": "user1",
  "password": "password123"
}
```

**Авторизация:**
```json
POST /api/auth/login
{
  "username": "user1",
  "password": "password123"
}
// Ответ:
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

JWT-токен передаётся в заголовке `Authorization: Bearer <token>`.

### Карты (`/api/cards`)

| Метод | Путь | Доступ | Описание |
|---|---|---|---|
| POST | `/api/cards` | ADMIN | Создание карты |
| GET | `/api/cards/{id}` | ADMIN | Получение карты по ID |
| GET | `/api/cards/user/{userId}` | ADMIN | Карты пользователя (с пагинацией) |
| PATCH | `/api/cards/{id}/status` | ADMIN | Изменение статуса карты |
| DELETE | `/api/cards/{id}` | ADMIN | Удаление карты |
| GET | `/api/cards/my` | USER, ADMIN | Мои карты (с пагинацией) |
| GET | `/api/cards/{id}/balance` | USER, ADMIN | Баланс карты |
| POST | `/api/cards/{id}/request-block` | USER | Запрос на блокировку карты |

**Создание карты (ADMIN):**
```json
POST /api/cards
{
  "cardNumber": "4111111111111111",
  "expirationDate": "2027-12-31",
  "ownerId": 2,
  "balance": 10000.00
}
```

**Ответ** (номер карты маскируется):
```json
{
  "id": 1,
  "maskedNumber": "**** **** **** 1111",
  "expirationDate": "2027-12-31",
  "status": "ACTIVE",
  "balance": 10000.00,
  "blockRequested": false
}
```

**Пагинация:**
```
GET /api/cards/my?page=0&size=10&sort=id,desc
```

**Статусы карт:**

| Статус | Описание |
|---|---|
| `ACTIVE` | Карта активна, доступны переводы |
| `BLOCKED` | Карта заблокирована |
| `EXPIRED` | Срок действия карты истёк |

### Переводы (`/api/transfers`)

| Метод | Путь | Доступ | Описание |
|---|---|---|---|
| POST | `/api/transfers` | USER, ADMIN | Перевод между своими картами |
| GET | `/api/transfers/card/{cardId}` | USER, ADMIN | История переводов по карте |

**Перевод:**
```json
POST /api/transfers
{
  "fromCardId": 1,
  "toCardId": 2,
  "amount": 500.00
}
```

Правила переводов:
- Обе карты должны принадлежать текущему пользователю
- Обе карты должны иметь статус `ACTIVE`
- На карте-отправителе должно быть достаточно средств

### Пользователи (`/api/users`)

| Метод | Путь | Доступ | Описание |
|---|---|---|---|
| GET | `/api/users` | ADMIN | Список всех пользователей |
| GET | `/api/users/{username}` | ADMIN | Получение пользователя по username |

## Безопасность

- Аутентификация через JWT-токен (срок действия — 24 часа)
- Пароли хранятся в виде BCrypt-хеша
- CSRF отключён (stateless API)
- Сессии не используются (`SessionCreationPolicy.STATELESS`)
- Номера карт в ответах API маскируются: `**** **** **** 1234`
- Доступ к картам проверяется — пользователь может видеть только свои карты

## Swagger UI

После запуска документация API доступна по адресу:

 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

Дополнительная документация доступна в папке `Docs`.

## Тестирование

```bash
./gradlew test
```

Юнит-тесты покрывают:
- Контроллеры (`AuthController`, `CardController`, `TransferController`, `UserController`)
- Сервисы (`CardServiceImpl`, `TransferServiceImpl`, `UserServiceImpl`, `UserDetailsServiceImpl`)
- Безопасность (`SecurityService`)

## Структура проекта

```
src/main/java/bankcards/
├── config/            # Конфигурация (Security, OpenAPI)
├── controller/        # REST-контроллеры
├── dto/               # Data Transfer Objects
├── entity/            # JPA-сущности
├── exception/         # Исключения и глобальный обработчик
├── repository/        # Spring Data JPA репозитории
├── security/          # JWT утилиты, фильтр, сервис проверки доступа
└── service/           # Бизнес-логика
```
