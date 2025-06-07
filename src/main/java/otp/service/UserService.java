package otp.service;

import otp.model.User;

public interface UserService {
    void register(String username, String password, String role);
    User getUserByUsername(String username);
}
