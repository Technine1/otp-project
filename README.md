# OTP Authentication Service

OTP-коды для подтверждения операций через Email, SMS, Telegram или файл.

## Быстрый старт

1. Установите Java 17+, PostgreSQL 17 и Maven.
2. Создайте БД `otp_service`, выполните `init.sql`.
3. Настройте конфигурации в `*.properties` (см. ниже).
4. Соберите и запустите сервер:
   ```bash
   mvn clean package
   java -jar target/otp-service-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

---

## Конфигурации

В `src/main/resources` разместите:

### `db.properties`

```
db.url=jdbc:postgresql://localhost:5432/otp_service
db.username=postgres
db.password=your_password
```

### `otp.properties`

```
otp.code_length=6
otp.ttl_seconds=120
```

### `jwt.properties`

```
JWT_SECRET=super-secret-key
JWT_EXPIRATION_MS=900000
```

### `email.properties`

```
email.username=you@example.com
email.password=your_password
email.from=you@example.com
mail.smtp.host=smtp.example.com
mail.smtp.port=587
mail.smtp.auth=true
mail.smtp.starttls.enable=true
```

### `sms.properties`

```
smpp.host=localhost
smpp.port=2775
smpp.system_id=smppclient1
smpp.password=password
smpp.system_type=OTP
smpp.source_addr=OTPService
```

### `telegram.properties`

```
telegram.bot_token=your-bot-token
telegram.chat_id=123456789
```

---

## API Команды

### Публичные

#### `POST /register`

Регистрация пользователя

```
username=user1,password=secret,role=USER
```

#### `POST /login`

Получение JWT

```
username=user1,password=secret
```

---

### Защищённые (JWT)

#### `POST /otp/generate`

```
Headers: Authorization: Bearer <token>
Body: operationId=confirm_1,channel=email
```

#### `POST /otp/validate`

```
Headers: Authorization: Bearer <token>
Body: operationId=confirm_1,code=123456
```

---

### Админ (JWT + роль ADMIN)

#### `POST /admin/config`

```
codeLength=6,ttlSeconds=90
```

#### `GET /admin/users`

#### `POST /admin/delete`

```
userId=3
```

---

## Тестирование

Рекомендуется использовать:

- Postman
- curl:

```bash
curl -X POST http://localhost:8080/register      --data "username=test,password=pass,role=USER"
```

Postman collection:  
[otp-service-postman-collection.json](docs/OTP_Service_API.postman_collection.json)

---

## Сборка проекта

```bash
mvn clean package
java -jar target/otp-service-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## Используемые библиотеки

- SLF4J + Logback — логирование
- Apache HttpClient 5 — Telegram
- JavaMail API — Email
- OpenSMPP — SMPP эмуляция
- JWT — токены (auth0)

---
