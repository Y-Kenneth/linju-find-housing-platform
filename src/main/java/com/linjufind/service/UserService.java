package com.linjufind.service;

import com.linjufind.dao.UserDao;
import com.linjufind.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User login(String username, String password) {
        return userDao.findByUsernameAndPassword(username, password);
    }

    public String register(String username, String email, String password, String nationality, String city) {
        if (username == null || username.isBlank()) return "Username is required.";
        if (email == null || email.isBlank()) return "Email is required.";
        if (password == null || password.isBlank()) return "Password is required.";

        if (userDao.findByUsername(username) != null) return "Username already taken.";
        if (userDao.findByEmail(email) != null) return "Email already registered.";

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setNationality(nationality);
        user.setCity(city);
        userDao.insert(user);
        return null; // null = success
    }
}
