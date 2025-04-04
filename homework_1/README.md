# FinanceTracker

**FinanceTracker** — это консольное приложение для управления личными финансами. Оно позволяет пользователям регистрироваться, входить в систему, управлять транзакциями, бюджетами, финансовыми целями, а также просматривать статистику и аналитику.

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

---

## Установка и запуск

1. **Клонирование репозитория**:
   ```bash
   git clone https://github.com/mortun5391/YLABStudy.git
   cd FinanceTracker
   ```

2. **Сборка проекта**:
   ```bash
   mvn clean package
   ```

3. **Запуск приложения**:
   ```bash
   java -jar target/FinanceTracker-1.0-SNAPSHOT.jar
   ```

---

## Использование

### 1. Регистрация и вход
- При первом запуске зарегистрируйтесь, выбрав пункт **1. Регистрация**.
- Введите email, пароль и имя.
- После регистрации войдите в систему, выбрав пункт **2. Вход**.

### 2. Управление транзакциями
- В главном меню выберите **1. Управление транзакциями**.
- Добавьте транзакцию, указав сумму, категорию, дату, описание и тип (доход/расход).
- Просмотрите список транзакций с возможностью фильтрации.

### 3. Управление бюджетом
- В главном меню выберите **2. Управление бюджетом**.
- Установите месячный бюджет, указав месяц и сумму.
- Просмотрите текущие расходы и остаток бюджета.

### 4. Управление целями
- В главном меню выберите **3. Управление целями**.
- Установите финансовую цель, указав название и целевую сумму.
- Просмотрите прогресс по цели.

### 5. Статистика и аналитика
- В главном меню выберите **4. Статистика и аналитика**.
- Просмотрите текущий баланс, доходы и расходы за период.
- Сформируйте отчет.

### 6. Административные функции
- Войдите в систему как администратор (см. раздел **Административный аккаунт**).
- В главном меню выберите **6. Просмотреть список пользователей**.
- Удалите или заблокируйте пользователя.

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

## Тестирование

Для запуска тестов выполните команду:
```bash
mvn test
```

---

## Авторы

- [mortun5391](https://github.com/mortun5391)

---
