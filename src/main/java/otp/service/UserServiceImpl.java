package otp.service;

import otp.dao.UserDao;
import otp.dao.UserDaoImpl;
import otp.model.User;
import otp.util.PasswordHasher;

import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserDao userDao = new UserDaoImpl();

    @Override
    public void register(String username, String password, String role) {
        Optional<User> existing = userDao.findByUsername(username);
        if (existing.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        if ("ADMIN".equalsIgnoreCase(role)) {
            boolean adminExists = userDao.findAll().stream()
                    .anyMatch(u -> "ADMIN".equalsIgnoreCase(u.getRole()));
            if (adminExists) {
                throw new RuntimeException("Administrator already exists");
            }
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(PasswordHasher.hash(password));
        user.setRole(role);
        userDao.create(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return userDao.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
