package dataaccess;

import models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    private final HashMap<String, User> users = new HashMap<>();

    public MemoryUserDAO() {
    }

    @Override
    public void createUser(String username, String password, String email) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User newUser = new User(username, hashedPassword, email);
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
