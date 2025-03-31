package com.financetracker.service;

import com.financetracker.model.User;
import com.financetracker.repository.UserRepository;

import java.util.Optional;

/**
 * Сервис для управления пользователями.
 * Предоставляет методы для регистрации, входа, удаления, блокировки пользователей,
 * а также для изменения их данных и просмотра профиля.
 */
public class UserService {
    private final UserRepository userRepository;

    /**
     * Конструктор для создания экземпляра UserService.
     *
     * @param userRepository репозиторий пользователей, который будет использоваться сервисом.
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param email    электронная почта пользователя. Не может быть null или пустой строкой.
     * @param password пароль пользователя. Не может быть null или пустой строкой.
     * @param name     имя пользователя. Не может быть null или пустой строкой.
     * @param role     роль пользователя. Не может быть null или пустой строкой.
     * @return true, если регистрация прошла успешно; false, если пользователь с таким email уже существует.
     * @throws IllegalArgumentException если любой из параметров равен null или пустой строке.
     */
    public boolean registerUser(String email, String password, String name, String role) {
        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                name == null || name.trim().isEmpty() ||
                role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Email, password, name, and role cannot be null or empty");
        }

        // Проверяем, существует ли пользователь с таким email
        if (userRepository.findUserByEmail(email).isPresent()) {
            return false;
        }

        // Создаем и сохраняем нового пользователя
        User user = new User(email, password, name, role);
        userRepository.saveUser(user);
        return true;
    }

    /**
     * Выполняет вход пользователя в систему.
     *
     * @param email    электронная почта пользователя. Не может быть null или пустой строкой.
     * @param password пароль пользователя. Не может быть null или пустой строкой.
     * @return true, если вход выполнен успешно; false, если пользователь не найден или пароль неверен.
     * @throws IllegalArgumentException если email или password равен null или пустой строке.
     * @throws IllegalStateException    если аккаунт пользователя заблокирован.
     */
    public boolean loginUser(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Email and password cannot be null or empty");
        }

        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if (userOptional.isEmpty() || !userOptional.get().getPassword().equals(password)) {
            return false;
        }

        User user = userOptional.get();
        if (user.getStatus().equalsIgnoreCase("banned")) {
            throw new IllegalStateException("Ваш аккаунт заблокирован!");
        }

        return true;
    }

    /**
     * Удаляет пользователя из системы по его идентификатору.
     *
     * @param id уникальный идентификатор пользователя.
     * @throws IllegalArgumentException если пользователь с указанным id не найден.
     */
    public void deleteUser(long id) {
        if (userRepository.findUserById(id).isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " not found");
        }
        userRepository.deleteUser(id);
    }

    /**
     * Блокирует пользователя по его идентификатору.
     *
     * @param id уникальный идентификатор пользователя.
     * @throws IllegalArgumentException если пользователь с указанным id не найден.
     */
    public void banUser(long id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
        user.setStatus("banned");
        userRepository.updateUser(user);
    }

    /**
     * Проверяет, совпадает ли пароль пользователя с указанным паролем.
     *
     * @param id       уникальный идентификатор пользователя.
     * @param password пароль для проверки.
     * @return true, если пароль совпадает; false, если нет.
     * @throws IllegalArgumentException если пользователь с указанным id не найден.
     */
    public boolean isPasswordEqual(long id, String password) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
        return user.getPassword().equals(password);
    }

    /**
     * Проверяет, имеет ли пользователь доступ администратора.
     *
     * @param id уникальный идентификатор пользователя.
     * @return true, если пользователь имеет роль администратора; false, если нет.
     * @throws IllegalArgumentException если пользователь с указанным id не найден.
     */
    public boolean hasAccess(long id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
        return user.getRole().equalsIgnoreCase("admin");
    }

    /**
     * Проверяет, существует ли пользователь с указанным идентификатором.
     *
     * @param id уникальный идентификатор пользователя.
     * @return true, если пользователь существует; false, если нет.
     */
    public boolean isUserExist(long id) {
        return userRepository.findUserById(id).isPresent();
    }

    /**
     * Изменяет email пользователя.
     *
     * @param id    уникальный идентификатор пользователя.
     * @param email новый email. Не может быть null или пустой строкой.
     * @throws IllegalArgumentException если email равен null или пустой строке.
     * @throws IllegalStateException    если пользователь не найден.
     */
    public void changeEmail(long id, String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new IllegalStateException("User with id " + id + " not found"));
        user.setEmail(email);
        userRepository.updateUser(user);
    }

    /**
     * Изменяет пароль пользователя.
     *
     * @param id       уникальный идентификатор пользователя.
     * @param password новый пароль. Не может быть null или пустой строкой.
     * @throws IllegalArgumentException если пароль равен null или пустой строке.
     * @throws IllegalStateException    если пользователь не найден.
     */
    public void changePassword(long id, String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new IllegalStateException("User with id " + id + " not found"));
        user.setPassword(password);
        userRepository.updateUser(user);
    }

    /**
     * Изменяет имя пользователя.
     *
     * @param id   уникальный идентификатор пользователя.
     * @param name новое имя. Не может быть null или пустой строкой.
     * @throws IllegalArgumentException если имя равно null или пустой строке.
     * @throws IllegalStateException    если пользователь не найден.
     */
    public void changeName(long id, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new IllegalStateException("User with id " + id + " not found"));
        user.setName(name);
        userRepository.updateUser(user);
    }

    /**
     * Отображает профиль пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return строка с информацией о профиле пользователя.
     * @throws IllegalStateException если пользователь не найден.
     */
    public String viewProfile(long id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new IllegalStateException("User with id " + id + " not found"));
        return (
                "\nСтатус: " + user.getRole() +
                        "\nИмя: " + user.getName() +
                        "\nEmail: " + user.getEmail() +
                        "\nПароль: ******** ");
    }

    /**
     * Отображает список всех пользователей в системе.
     * Доступно только для администраторов.
     *
     * @param id уникальный идентификатор пользователя, запрашивающего список.
     * @throws IllegalStateException если пользователь не является администратором.
     */
    public void viewUsersList(long id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new IllegalStateException("User with id " + id + " not found"));

        if (!user.getRole().equalsIgnoreCase("admin")) {
            System.out.println("Только администратор может просматривать список пользователей!");
            return;
        }

        System.out.println("Список пользователей: ");
        System.out.printf("%-10s %-20s %-15s %-10s %-10s%n", "ID", "Email", "Имя", "Роль", "Статус");

        // Данные пользователей
        for (User u : userRepository.getUsers()) {
            System.out.printf(
                    "%-10s %-20s %-15s %-10s %-10s%n",
                    u.getId(),
                    u.getEmail(),
                    u.getName(),
                    u.getRole(),
                    u.getStatus()
            );
        }
    }

    /**
     * Возвращает пользователя по его email.
     *
     * @param email электронная почта пользователя.
     * @return объект User, если пользователь найден; null, если пользователь не найден.
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id уникальный идентификатор пользователя.
     * @return объект User, если пользователь найден; null, если пользователь не найден.
     */
    public Optional<User> getUserById(long id) {
        return userRepository.findUserById(id);
    }
}