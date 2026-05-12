package dataaccess;

import models.User;

public interface UserDAO {
    void createUser(String username, String password, String email);
    User getUser(String username);
    User getUser(User user);
    void clear();
}
