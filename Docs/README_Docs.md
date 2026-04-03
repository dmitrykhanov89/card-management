# Bank Cards API Documentation

Документация по Backend API для управления банковскими картами.  
Содержит OpenAPI спецификацию и примеры работы с эндпоинтами.

---

## 📄 OpenAPI спецификация

Файл `openapi.yaml` находится в директории `docs` и описывает все доступные эндпоинты API.
Для авторизации в Swagger UI нажмите **Authorize** и введите JWT-токен.

- OpenAPI YAML: [http://localhost:8080/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml)
- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> Swagger UI позволяет тестировать все эндпоинты прямо из браузера.

---

## 🔑 Аутентификация

Все защищённые эндпоинты требуют JWT-токен:

1. **Регистрация пользователя**:  
   `POST /api/auth/register` — создаёт пользователя с ролью `USER`.

2. **Логин**:  
   `POST /api/auth/login` — возвращает JWT токен:

   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   }