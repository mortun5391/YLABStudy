package com.financetracker.service;

import com.financetracker.model.User;
import com.financetracker.repository.UserRepository;

public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param email электронная почта пользователя. Не может быть null или пустой строкой.
     * @param password пароль пользователя. Не может быть null или пустой строкой.
     * @param name имя пользователя. Не может быть null или пустой строкой.
     * @param role статус пользователя. Не может быть null или пустой строкой.
     * @return true, если регистрация прошла успешно; false, если пользователь с таким email уже существует.
     * @throws IllegalArgumentException если любой из параметров равен null или пустой строке.
     */
    public boolean registerUser(String email, String password, String name, String role) {
        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                name == null || name.trim().isEmpty() ||
                role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Email, password, name, and status cannot be null or empty");
        }
        if (userRepository.findUserByEmail(email) != null) {
            return false;
        }
        userRepository.saveUser(new User(email, password, name, role));
        return true;
    }

    /**
     * Выполняет вход пользователя в систему.
     *
     * @param email электронная почта пользователя. Не может быть null или пустой строкой.
     * @param password пароль пользователя. Не может быть null или пустой строкой.
     * @return true, если вход выполнен успешно; false, если пользователь не найден или пароль неверен.
     * @throws IllegalArgumentException если email или password равен null или пустой строке.
     */
    public boolean loginUser(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Email and password cannot be null or empty");
        }
        User user = userRepository.findUserByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            return false;
        }
        if (user.getStatus().equals("banned")) {
            throw new IllegalStateException("Ваш аккаунт заблокирован!");
        }
        return true;
    }

    /**
     * Удаляет пользователя из системы по его идентификатору.
     * Если удаляемый пользователь является текущим, выполняется выход из системы.
     *
     * @param id уникальный идентификатор пользователя.
     * @throws IllegalArgumentException если пользователь с указанным id не найден.
     */
    public void deleteUser(String id) {
        if (userRepository.findUserById(id) == null) {
            throw new IllegalArgumentException("User with id " + id + " not found");
        }
        userRepository.deleteUser(id);
    }

    public void banUser(String id) {
        if (userRepository.findUserById(id) == null) {
            throw new IllegalArgumentException("User with id " + id + " not found");
        }
        userRepository.findUserById(id).setStatus("banned");
    }

    public boolean isPasswordEqual(String id, String password) {
        if (userRepository.findUserById(id) == null) {
            throw new IllegalArgumentException("User with id " + id + " not found");
        }
        return userRepository.findUserById(id).getPassword().equals(password);
    }

    public boolean hasAccess(String id) {
        if (userRepository.findUserById(id) == null) {
            throw new IllegalArgumentException("User with id " + id + " not found");
        }
        return userRepository.findUserById(id).getRole().equals("admin");
    }

    public boolean isUserExist(String id) {
        return userRepository.findUserById(id) != null;
    }

    /**
     * Изменяет email текущего пользователя.
     *
     * @param email новый email. Не может быть null или пустой строкой.
     * @throws IllegalArgumentException если email равен null или пустой строке.
     * @throws IllegalStateException если текущий пользователь не аутентифицирован.
     */
    public void changeEmail(String id, String email) {
        User user = userRepository.findUserById(id);
        userRepository.deleteUser(id);
        user.setEmail(email);
        userRepository.saveUser(user);
    }

    /**
     * Изменяет пароль текущего пользователя.
     *
     * @param password новый пароль. Не может быть null или пустой строкой.
     * @throws IllegalArgumentException если пароль равен null или пустой строке.
     * @throws IllegalStateException если текущий пользователь не аутентифицирован.
     */
    public void changePassword(String id,String password) {
        userRepository.findUserById(id).setPassword(password);
    }

    /**
     * Изменяет имя текущего пользователя.
     *
     * @param name новое имя. Не может быть null или пустой строкой.
     * @throws IllegalArgumentException если имя равно null или пустой строке.
     * @throws IllegalStateException если текущий пользователь не аутентифицирован.
     */
    public void changeName(String id, String name) {
        userRepository.findUserById(id).setName(name);
    }

    /**
     * Отображает профиль текущего пользователя.
     * Выводит статус, имя, email и скрытый пароль.
     *
     * @throws IllegalStateException если текущий пользователь не аутентифицирован.
     */
    public String viewProfile(String id) {
        User user = userRepository.findUserById(id);
        return (
                "\nСтатус: " + user.getRole() +
                        "\nИмя: " + user.getName() +
                        "\nEmail: " + user.getEmail() +
                        "\nПароль: ******** ");
    }

    /**
     * Отображает список всех пользователей в системе.
     * Форматированный вывод включает ID, Email, Имя и Статус пользователя.
     * Метод доступен только для аутентифицированных пользователей.
     */
    public void viewUsersList(String id) {
        if (!userRepository.findUserById(id).getRole().equals("admin")) {
            System.out.println("Только администратор может просматривать список пользователей!");
            return;
        }
        System.out.println("Список пользователей: ");
        System.out.printf("%-10s %-20s %-15s %-10s %-10s%n", "ID", "Email", "Имя", "Роль", "Статус");

        // Данные пользователей
        for (User user : userRepository.getUsers()) {
            System.out.printf(
                    "%-10s %-20s %-15s %-10s %-10s%n",
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getRole(),
                    user.getStatus()
            );
        }
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public User getUserById(String id) {
        return userRepository.findUserById(id);
    }
}