package otp.service;

import otp.model.User;

public interface AuthService {
    String login(String username, String password);
    User verifyToken(String token);
}
