package dataaccess;

import models.User;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    private final HashMap<String, User> users = new HashMap<>();

    public MemoryUserDAO() {
    }

    @Override
    public void createUser(String username, String password, String email) {
        User newUser = new User(username, password, email);
        users.put(username, newUser);
    }

    @Override
    public User getUser(String username) {
        return users.get(username);
    }

    @Override
    public User getUser(User user) {
        return users.get(user.username());
    }

    @Override
    public void clear() {
        users.clear();
    }
}
