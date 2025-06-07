package otp.service;

import otp.model.User;
import otp.util.JwtUtil;
import otp.util.PasswordHasher;

public class AuthServiceImpl implements AuthService {
    private final UserService userService = new UserServiceImpl();

    @Override
    public String login(String username, String password) {
        User user = userService.getUserByUsername(username);
        if (!PasswordHasher.check(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        return JwtUtil.createToken(user);
    }

    @Override
    public User verifyToken(String token) {
        return JwtUtil.decodeToken(token);
    }
}
