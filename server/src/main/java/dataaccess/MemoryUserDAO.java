package dataaccess;

import models.User;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    private final HashMap<String, User> users = new HashMap<>();

    public MemoryUserDAO() {
    }

    @Override
    public void createUser(User user) {
        users.put(user.username(), user);
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
    public void deleteUser(String username) {
        users.remove(username);
    }

    @Override
    public void clear() {
        users.clear();
    }
}
