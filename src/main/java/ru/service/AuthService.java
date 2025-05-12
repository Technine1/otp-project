package ru.service;

import ru.api.common.Status;
import ru.exception.OTPException;
import ru.model.Role;
import ru.model.User;
import ru.repository.CrudRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;

public class AuthService {

    private final TokenService tokenService;
    private final CrudRepository repository;
    public AuthService(TokenService tokenService, CrudRepository repository) {
        this.tokenService = tokenService;
        this.repository = repository;
    }

    /**
     * Авторизация пользователя
     */
    public String login(String login, String password) {
        User user = repository.getById(login, User.class);
        if (user == null || !verify(password, user.getPassword())) {
            throw new OTPException(Status.BAD_REQUEST, "Неверный логин или пароль");
        }
        return tokenService.generateToken(login, user.getRole());
    }

    /**
     * Создать нового пользователя
     */
    public boolean create(User user) {
        if (repository.getById(user.getLogin(), User.class) != null) {
            throw new OTPException(Status.BAD_REQUEST, "Профиль " + user.getLogin() + " уже существует");
        }
        user.setPassword(hashPassword(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        if (user.getRole().equals(Role.ADMIN)) {
            List<User> admins = repository.getByField("role", user.getRole(), User.class);
            if (!admins.isEmpty()) {
                throw new OTPException(Status.BAD_REQUEST, "Невозможно создать пользователя с ролью " + user.getRole().name());
            }
        }
        return repository.save(user);
    }

    private static String hashPassword(String password) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return hashPassword(password, salt);
    }

    private static String hashPassword(String password, byte[] salt) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new OTPException(Status.INTERNAL_SERVER_ERROR, e);
        }
        md.update(salt);
        byte[] hash = md.digest(password.getBytes());
        return getEncoder().encodeToString(salt) + ":" + getEncoder().encodeToString(hash);
    }

    private static boolean verify(String password, String hashedPassword) {
        byte[] salt = getDecoder().decode(hashedPassword.split(":")[0]);
        return hashedPassword.equals(hashPassword(password, salt));
    }
}
