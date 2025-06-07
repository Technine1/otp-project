package otp.dao;

import otp.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    void create(User user);
    Optional<User> findByUsername(String username);
    void deleteById(int userId);
    List<User> findAll();
}