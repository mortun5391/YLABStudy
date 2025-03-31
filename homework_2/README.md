# FinanceTracker

**FinanceTracker** — это консольное приложение для управления личными финансами. Оно позволяет пользователям регистрироваться, входить в систему, управлять транзакциями, бюджетами, финансовыми целями, а также просматривать статистику и аналитику. Приложение использует PostgreSQL для хранения данных и Liquibase для управления миграциями базы данных.

---

## Основные функции

1. **Регистрация и вход**:
    - Пользователи могут зарегистрироваться, указав email, пароль и имя.
    - После регистрации можно войти в систему.

2. **Управление транзакциями**:
    - Добавление, удаление и редактирование транзакций.
    - Просмотр транзакций с возможностью фильтрации по дате, категории и типу (доход/расход).

3. **Управление бюджетом**:
    - Установка месячного бюджета.
    - Просмотр текущих расходов и остатка бюджета.

4. **Управление финансовыми целями**:
    - Установка финансовых целей (например, накопить на отпуск).
    - Просмотр прогресса по целям.

5. **Статистика и аналитика**:
    - Просмотр текущего баланса.
    - Анализ доходов и расходов за период.
    - Формирование отчетов.

6. **Административные функции**:
    - Просмотр списка пользователей (доступно только администратору).
    - Удаление или блокировка пользователей.

---

## Требования

- **Java 17** или выше.
- **Maven** для сборки проекта.
- **Docker** и **Docker Compose** для запуска базы данных.

---

## Установка и запуск

### 1. Клонирование репозитория
```bash
git clone https://github.com/mortun5391/YLABStudy.git
cd FinanceTracker
```

### 2. Сборка проекта
```bash
mvn clean package
```

### 3. Запуск приложения с использованием Docker Compose
1. Убедитесь, что Docker и Docker Compose установлены и запущены.
2. Запустите базу данных и приложение с помощью Docker Compose:
   ```bash
   docker-compose up --build
   ```
   Это развернет PostgreSQL с настройками, указанными в `docker-compose.yml`, и запустит приложение.

### 4. Запуск приложения без Docker
Если вы хотите запустить приложение локально (без Docker), убедитесь, что у вас установлена и запущена PostgreSQL с параметрами, указанными в `application.yml`.

---

## Конфигурация

### Конфигурация базы данных
Все параметры подключения к базе данных и настройки Liquibase задаются в файле `application.yml`. Пример конфигурации:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/financetracker
    username: financetracker_user
    password: financetracker_password
    driver-class-name: org.postgresql.Driver
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/changelog-master.xml
    default-schema: finance_schema
```

### Docker Compose
Файл `docker-compose.yml` содержит конфигурацию для запуска PostgreSQL в Docker:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:13
    container_name: financetracker_db
    environment:
      POSTGRES_USER: financetracker_user
      POSTGRES_PASSWORD: financetracker_password
      POSTGRES_DB: financetracker
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

---

## Миграции базы данных

### Liquibase
Миграции базы данных управляются с помощью Liquibase. Скрипты миграции находятся в директории `src/main/resources/db/changelog`.

- **Создание таблиц**: Скрипты для создания таблиц находятся в `changelog-create-tables.xml`.
- **Предзаполнение данными**: Скрипты для предзаполнения данными находятся в `changelog-insert-data.xml`.

Пример структуры файла миграции:

```xml
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
    http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd">

    <changeSet id="1" author="mortun5391">
        <createTable tableName="users" schemaName="finance_schema">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="email" type="VARCHAR(255)">
                <constraints unique="true" nullable="false"/>
            </column>
            <column name="password" type="VARCHAR(255)">
                <constraints nullable="false"/>
            </column>
            <column name="name" type="VARCHAR(255)">
                <constraints nullable="false"/>
            </column>
        </createTable>
    </changeSet>
</databaseChangeLog>
```

---

## Тестирование

Для тестирования используется **Testcontainers**, который запускает PostgreSQL в Docker-контейнере. Это позволяет изолировать тесты от основной базы данных.

### Запуск тестов
```bash
mvn test
```

---

## Административный аккаунт

Для доступа к административным функциям используйте следующие данные:

- **Email**: `admin@example.com`
- **Пароль**: `admin123`

---

## Примеры использования

### Регистрация и вход
```plaintext
1. Регистрация
2. Вход
0. Выход
Выберите действие: 1
Введите email: test@example.com
Введите пароль: password123
Подтвердите пароль: password123
Введите имя: Test User
Регистрация прошла успешно
1. Регистрация
2. Вход
0. Выход
Выберите действие: 2
Введите email: test@example.com
Введите пароль: password123
Вход выполнен успешно
```

### Добавление транзакции
```plaintext
Выберите действие: 1
Введите сумму: 1000
Введите категорию: Food
Введите дату (гггг-мм-дд): 2025-03-05
Введите описание: Cafe
Это доход? (true/false): false
Транзакция добавлена
```

### Установка бюджета
```plaintext
Месячный бюджет не установлен
1. Установить месячный бюджет
0. Выход
Выберите действие: 1
Введите месяц (гггг-мм) :2025-03-05
Введите сумму бюджета: 1000
Месячный бюджет установлен!
```

---

## Авторы

- [mortun5391](https://github.com/mortun5391)

---

## Лицензия

Этот проект распространяется под лицензией MIT. Подробности см. в файле [LICENSE](LICENSE).
