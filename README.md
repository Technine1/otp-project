# OTP Service

 Простое Java-приложение для генерации, проверки и управления одноразовыми паролями (OTP) через HTTP API.

---

## Стек технологий

- Java 17+
- HTTP Server (встроенный в JDK)
- Maven
- Jackson (JSON-парсинг)
- PostgreSQL (при необходимости)
- Flyway (миграции)
- JWT (аутентификация)
- HikariCP (пул соединений)
- SLF4J (логирование)

---

## 🔧 Установка и запуск

### 1. Склонируй репозиторий

```bash
git clone https://github.com/your-username/otp-service.git
cd otp-service
```

### 2. Собери проект

```bash
mvn clean install
```

### 3. Запусти сервер

```bash
mvn exec:java -Dexec.mainClass="ru.technine1.otp.App"
```

или запусти `App.main()` из IntelliJ

---

## 📘 API Эндпоинты

### `/auth`

| Метод | Путь        | Описание                          |
|-------|-------------|-----------------------------------|
| GET   | /auth       | Проверка доступности              |
| POST  | /auth       | Логин, получение JWT              |
| POST  | /auth/new   | Регистрация нового пользователя   |

###  `/admin`

| Метод | Путь              | Описание                         |
|-------|-------------------|----------------------------------|
| GET   | /admin/users      | Получить список пользователей    |
| DELETE| /admin/users/{id} | Удалить пользователя             |
| PUT   | /admin/otp        | Обновить OTP                     |
| DELETE| /admin/otp/{id}   | Удалить OTP                      |

---

## Структура проекта

```
src/
├── main/
│   ├── java/
│   │   └── ru/
│   │       └── technine1/
│   │           ├── otp/
│   │           │   ├── App.java
│   │           │   ├── api/
│   │           │   │   └── AuthController.java
│   │           │   ├── common/
│   │           │   │   └── AdminController.java
│   │           │   └── api/common/...
│   └── resources/
│       └── application.properties
```

---

## Безопасность

- Используется JWT для аутентификации
- Обработка ошибок через `OTPException`
- Валидация входных параметров с аннотациями

---

## Лицензия

Проект доступен под MIT лицензией.

