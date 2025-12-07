package com.learn.sso.customer.dao;

import com.learn.sso.customer.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UserDAO {
    private static UserDAO instance;
    private Map<Integer, User> users;
    private AtomicInteger idGenerator;

    private UserDAO() {
        users = new HashMap<>();
        idGenerator = new AtomicInteger(1);

        // Initialize with sample data
        addUser(new User(idGenerator.getAndIncrement(), "John Doe", "john@example.com", "Admin"));
        addUser(new User(idGenerator.getAndIncrement(), "Jane Smith", "jane@example.com", "User"));
        addUser(new User(idGenerator.getAndIncrement(), "Bob Johnson", "bob@example.com", "User"));
    }

    public static synchronized UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserById(int id) {
        return users.get(id);
    }

    public void addUser(User user) {
        if (user.getId() == 0) {
            user.setId(idGenerator.getAndIncrement());
        }
        users.put(user.getId(), user);
    }

    public void updateUser(User user) {
        users.put(user.getId(), user);
    }

    public void deleteUser(int id) {
        users.remove(id);
    }
}